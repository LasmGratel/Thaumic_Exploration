package flaxbeard.thaumicexploration.block;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import flaxbeard.thaumicexploration.ThaumicExploration;
import flaxbeard.thaumicexploration.common.ConfigTX;
import flaxbeard.thaumicexploration.tile.TileEntityReplicator;
import gnu.trove.set.hash.THashSet;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.utils.InventoryUtils;

public class BlockReplicator extends BlockContainer {

    // public static ArrayList<Integer> allowedItems = new ArrayList<Integer>();

    public IIcon[] icon = new IIcon[3];

    public BlockReplicator() {
        super(Material.iron);
    }

    @Override
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6) {
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
        TileEntity tileEntity = par1World.getTileEntity(par2, par3, par4);
        if (tileEntity != null && tileEntity instanceof TileEntityReplicator) {
            if (((TileEntityReplicator) tileEntity).getStackInSlot(0) != null
                    && ((TileEntityReplicator) tileEntity).getStackInSlot(0).stackSize > 0) {
                InventoryUtils.dropItems(par1World, par2, par3, par4);
            }
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block par5) {
        boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if ((tileEntity != null && tileEntity instanceof TileEntityReplicator)) {
            TileEntityReplicator ped = (TileEntityReplicator) tileEntity;
            ped.updateRedstoneState(flag);
        }
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7,
            float par8, float par9) {
        if (world.isRemote) {
            return true;
        }
        int metadata = world.getBlockMetadata(x, y, z);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if ((tileEntity instanceof TileEntityReplicator)) {
            TileEntityReplicator ped = (TileEntityReplicator) tileEntity;
            if (ped.crafting && (player.getCurrentEquippedItem() == null
                    || !(player.getCurrentEquippedItem().getItem() instanceof ItemWandCasting))) {
                ped.cancelCrafting();
            }

            if (ped.getStackInSlot(0) != null && (player.getCurrentEquippedItem() == null
                    || !(player.getCurrentEquippedItem().getItem() instanceof ItemWandCasting))) {
                ItemStack itemstack = ped.getStackInSlot(0);

                if (itemstack.stackSize > 0) {
                    float f = world.rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = world.rand.nextFloat();
                    EntityItem entityitem;
                    int k1 = world.rand.nextInt(21) + 10;

                    k1 = itemstack.stackSize;

                    entityitem = new EntityItem(
                            world,
                            (double) ((float) x + f),
                            (double) ((float) y + f1),
                            (double) ((float) z + f2),
                            new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
                    float f3 = 0.05F;
                    entityitem.motionX = (double) ((float) world.rand.nextGaussian() * f3);
                    entityitem.motionY = (double) ((float) world.rand.nextGaussian() * f3 + 0.2F);
                    entityitem.motionZ = (double) ((float) world.rand.nextGaussian() * f3);

                    if (itemstack.hasTagCompound()) {
                        entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                    }
                    world.spawnEntityInWorld(entityitem);
                }
                TileEntity te = world.getTileEntity(x, y, z);
                ItemStack template = ((TileEntityReplicator) te).getStackInSlot(0).copy();
                if (template.stackSize > 0) {
                    template.stackSize = template.stackSize -= 1;

                    ((TileEntityReplicator) te).setInventorySlotContents(0, template);
                } else {
                    ((TileEntityReplicator) te).setInventorySlotContents(0, null);
                }
                world.markBlockForUpdate(x, y, z);
                world.playSoundEffect(
                        x,
                        y,
                        z,
                        "random.pop",
                        0.2F,
                        ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.5F);

                return true;
            }
            if (player.getCurrentEquippedItem() != null && isItemAllowed(player.getCurrentEquippedItem())) {
                // IN
                ItemStack i = player.getCurrentEquippedItem().copy();
                i.stackSize = 0;
                ped.setInventorySlotContents(0, i);
                // player.getCurrentEquippedItem().stackSize -= 1;
                if (player.getCurrentEquippedItem().stackSize == 0) {
                    player.setCurrentItemOrArmor(0, null);
                }
                // player.inventory.onInventoryChanged();
                world.markBlockForUpdate(x, y, z);
                world.playSoundEffect(
                        x,
                        y,
                        z,
                        "random.pop",
                        0.2F,
                        ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.6F);

                return true;
            }
        }

        return true;
    }

    private static final Set<Item> VANILLA_WOOD = new THashSet<>(
            Arrays.asList(
                    Item.getItemFromBlock(Blocks.wooden_slab),
                    Item.getItemFromBlock(Blocks.birch_stairs),
                    Item.getItemFromBlock(Blocks.oak_stairs),
                    Item.getItemFromBlock(Blocks.jungle_stairs),
                    Item.getItemFromBlock(Blocks.spruce_stairs),
                    Item.getItemFromBlock(Blocks.log),
                    Item.getItemFromBlock(Blocks.log2),
                    Item.getItemFromBlock(Blocks.planks)));

    private static final Set<Item> VANILLA_REPLICATE_ITEMS = new THashSet<>(
            Arrays.asList(
                    Item.getItemFromBlock(Blocks.mossy_cobblestone),
                    Item.getItemFromBlock(Blocks.stone_slab),
                    Item.getItemFromBlock(Blocks.sand),
                    Item.getItemFromBlock(Blocks.sandstone),
                    Item.getItemFromBlock(Blocks.sandstone_stairs),
                    Item.getItemFromBlock(Blocks.brick_block),
                    Item.getItemFromBlock(Blocks.brick_stairs),
                    Item.getItemFromBlock(Blocks.stonebrick),
                    Item.getItemFromBlock(Blocks.stone_brick_stairs),
                    Item.getItemFromBlock(Blocks.nether_brick),
                    Item.getItemFromBlock(Blocks.nether_brick_stairs),
                    Item.getItemFromBlock(Blocks.soul_sand),
                    Item.getItemFromBlock(Blocks.gravel),
                    Item.getItemFromBlock(Blocks.grass),
                    Item.getItemFromBlock(Blocks.glass),
                    Item.getItemFromBlock(Blocks.dirt),
                    Item.getItemFromBlock(Blocks.snow),
                    Item.getItemFromBlock(Blocks.clay),
                    Item.getItemFromBlock(Blocks.hardened_clay)));

    private boolean isItemAllowed(ItemStack stack) {
        AspectList ot = ThaumcraftCraftingManager.getObjectTags(stack);
        ot = ThaumcraftCraftingManager.getBonusTags(stack, ot);

        THashSet<String> oreNames = Arrays.stream(OreDictionary.getOreIDs(stack)).mapToObj(OreDictionary::getOreName)
                .collect(Collectors.toCollection(THashSet::new));

        if (oreNames.contains("logWood")) {
            if (stack.getItem() == Item.getItemFromBlock(ConfigBlocks.blockMagicalLog)) {
                return false;
            }
            return ot.getAspects().length > 0;
        } else if (oreNames.contains("treeLeaves")) {
            if (stack.getItem() != Item.getItemFromBlock(ConfigBlocks.blockMagicalLeaves)) {
                return false;
            }
            return ot.getAspects().length > 0;
        } else if (oreNames.contains("slabWood") || oreNames.contains("stairWood") || oreNames.contains("plankWood")) {
            if (stack.getItem() == Item.getItemFromBlock(ConfigBlocks.blockWoodenDevice)) {
                if (ConfigTX.allowMagicPlankReplication)
                    return stack.getItemDamage() == 6 || stack.getItemDamage() == 7;
                else return false;
            }

            if (ConfigTX.allowModWoodReplication) {
                return ot.getAspects().length > 0;
            } else {
                return VANILLA_WOOD.contains(stack.getItem());
            }
        } else if (oreNames.contains("stone") || oreNames.contains("cobblestone")) {
            if (ConfigTX.allowModStoneReplication) {
                return ot.getAspects().length > 0;
            } else {
                return stack.getItem() == Item.getItemFromBlock(Blocks.stone)
                        || stack.getItem() == Item.getItemFromBlock(Blocks.cobblestone);
            }
        }

        return VANILLA_REPLICATE_ITEMS.contains(stack.getItem());
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public int getRenderType() {
        return ThaumicExploration.replicatorRenderID;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {
        super.registerBlockIcons(ir);
        this.icon[0] = ir.registerIcon("thaumicexploration:replicatorBottom");
        this.icon[1] = ir.registerIcon("thaumicexploration:replicator");
        this.icon[2] = ir.registerIcon("thaumicexploration:replicatorTop");
    }

    @Override
    public IIcon getIcon(IBlockAccess iblockaccess, int i, int j, int k, int side) {
        if (side == 0 || side == 1) {
            return this.icon[0];
        }
        return this.icon[1];
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        // TODO Auto-generated method stub
        return new TileEntityReplicator();
    }
}
