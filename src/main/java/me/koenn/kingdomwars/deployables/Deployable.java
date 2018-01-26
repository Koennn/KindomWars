package me.koenn.kingdomwars.deployables;

import me.koenn.core.misc.ReflectionHelper;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.deployables_OLD.DeployableBlock;
import me.koenn.kingdomwars.util.DeployableHelper;
import me.koenn.kingdomwars.util.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.koenn.kingdomwars.util.NBTUtil.getChildTag;

@SuppressWarnings("deprecation")
public class Deployable {

    /**
     * The blocks that the deployable will replace.
     */
    private final List<DeployableBlock> oldBlocks = new ArrayList<>();

    /**
     * The blocks that make up the deployable.
     */
    private final List<Location> deployableBlocks = new ArrayList<>();

    /**
     * The location of the deployable.
     */
    private final Location location;

    /**
     * The owner of the deployable.
     */
    private final Player owner;

    /**
     * The type of deployable.
     */
    private final DeployableType type;

    /**
     * The executor to handle the logic of the deployable.
     */
    private final DeployableExecutor executor;

    /**
     * The id of the repeating update task.
     */
    private int taskId;

    /**
     * Constructor to create a new deployable at a set location.
     *
     * @param location Location to put it at
     * @param owner    Owner of the deployable
     * @param type     Type of deployable
     */
    public Deployable(Location location, Player owner, DeployableType type) {
        this.location = location;
        this.owner = owner;
        this.type = type;
        this.executor = (DeployableExecutor) ReflectionHelper.newInstance(type.getExecutor(), null);
    }

    /**
     * Construct the deployable at it's location.
     *
     * @return true if the construction was successful.
     */
    public boolean construct() {
        //Load the blocks from the NBT tag.
        HashMap<DeployableBlock, Integer> blocks = this.loadBlocks();
        if (blocks == null) {
            return false;
        }

        int highestDelay = 0;

        //Loop over all blocks.
        for (DeployableBlock block : blocks.keySet()) {
            //Get the placement delay for that block.
            int delay = blocks.get(block);

            //Calculate if it's the highest delay.
            highestDelay = delay > highestDelay ? delay : highestDelay;

            //Schedule the place task with the block's delay.
            Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> {

                //Get the block's offset.
                Location place = location.clone().add(block.getOffset());

                //Get the block's old data byte.
                byte oldData = place.getBlock().getData();

                //Set the block to the new type.
                place.getBlock().setType(block.getType());

                //Check if the data is real.
                if (block.getData() == -1) {

                    //If not, set the block to it's old data.
                    place.getBlock().setData(oldData);
                } else {

                    //If so, set the blocks to it's new data.
                    place.getBlock().setData(block.getData());
                }

                //Add the block to the list of blocks.
                this.deployableBlocks.add(place);

            }, delay);
        }

        //Schedule the init method with the highest delay.
        Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), this::init, highestDelay);
        return true;
    }

    /**
     * Initialize the deployable, must be called after construction has completed.
     */
    private void init() {
        //Call the executor's init method.
        this.executor.init(this.owner, this);

        //Schedule a repeating task to call the executor's update method.
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this.executor::update, 0, 5);
    }

    /**
     * Load all the blocks from NBT into a HashMap.
     *
     * @return HashMap with all blocks making up the deployable
     */
    private HashMap<DeployableBlock, Integer> loadBlocks() {
        //Create the HashMap.
        HashMap<DeployableBlock, Integer> result = new HashMap<>();

        //Get the CompoundTag.
        CompoundTag construction = this.type.getConstruction();

        //Get the list of build phases.
        List<Tag> phases = getChildTag(construction, "phases", ListTag.class).getValue();

        //Loop over the phases.
        for (Tag phaseTag : phases) {

            //Get the delay for this phase.
            int delay = getChildTag((CompoundTag) phaseTag, "delay", IntTag.class).getValue();

            //Get the list of blocks in this phase.
            List<Tag> blocks = getChildTag((CompoundTag) phaseTag, "blocks", ListTag.class).getValue();

            //Loop over the blocks.
            for (Tag blockTag : blocks) {

                //Create the DeployableBlock out of the data.
                DeployableBlock block = NBTUtil.getBlock((CompoundTag) blockTag);

                //Calculate the block's offset.
                Vector offset = DeployableHelper.rotateOffsetTowards(block.getOffset(), DeployableHelper.getPlayerDirection(this.owner));

                //Set the offset in the DeployableBlock.
                block.setOffset(offset);

                //Check if the block can be placed.
                if (!this.testBlockPlace(offset)) {

                    //If not, terminate this method and return null.
                    return null;
                }

                //Put the resulting DeployableBlock with it's delay in the HashMap.
                result.put(block, delay);
            }
        }

        //Return the resulting HashMap.
        return result;
    }

    /**
     * Test whether it's possible to place a block at a certain offset.
     *
     * @param offset Offset to place at
     * @return true if the block can be placed
     */
    private boolean testBlockPlace(Vector offset) {
        //Get the block that is currently at this offset.
        Block replace = location.clone().add(offset).getBlock();

        //Check if it's air or tallgrass.
        if (!replace.getType().equals(Material.AIR) && !replace.getType().equals(Material.LONG_GRASS)) {

            //Clear the oldBlocks and return false.
            this.oldBlocks.clear();
            return false;
        }

        //Add the block to the oldBlocks and return true.
        this.oldBlocks.add(new DeployableBlock(replace));
        return true;
    }

    /**
     * Get the deployable's location.
     *
     * @return deployable's location
     */
    public Location getLocation() {
        return location;
    }
}
