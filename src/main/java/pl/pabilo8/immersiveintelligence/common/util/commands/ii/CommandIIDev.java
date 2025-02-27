package pl.pabilo8.immersiveintelligence.common.util.commands.ii;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces.IAdvancedFluidItem;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import pl.pabilo8.immersiveintelligence.api.MultipleRayTracer;
import pl.pabilo8.immersiveintelligence.api.bullets.BulletHelper;
import pl.pabilo8.immersiveintelligence.api.utils.vehicles.IVehicleMultiPart;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;
import pl.pabilo8.immersiveintelligence.common.entity.EntityParachute;
import pl.pabilo8.immersiveintelligence.common.entity.bullets.EntityBullet;
import pl.pabilo8.immersiveintelligence.common.util.IIExplosion;
import pl.pabilo8.immersiveintelligence.common.world.IIWorldGen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Pabilo8
 * @since 23-06-2020
 */
public class CommandIIDev extends CommandBase
{
	private static Set<String> options = new HashSet<>();

	static
	{
		options.add("help");
		options.add("slowmo");
		options.add("zawarudo");
		options.add("bulletspeed");
		options.add("killbullets");
		options.add("killvehicles");
		options.add("killitems");
		options.add("killhanses");
		options.add("world_setup");
		options.add("tpd");
		options.add("decaybullets");
		options.add("test_enemies");
		options.add("explosion");
		options.add("nuke");
		options.add("power");
		options.add("tree");
		options.add("parachute");
		options.add("deth");
	}

	/**
	 * Gets the name of the command
	 */
	@Nonnull
	@Override
	public String getName()
	{
		return "dev";
	}

	/**
	 * Gets the usage string for the command.
	 */
	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender)
	{
		return "Executes an Immersive Intelligence command, for more info use /ii dev help";
	}

	/**
	 * Callback for when the command is executed
	 */
	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
	{
		if(args.length > 0)
		{
			Entity senderEntity = sender.getCommandSenderEntity();
			switch(args[0])
			{
				case "help":
				{
					sender.sendMessage(new TextComponentString("Executes an Immersive Intelligence command, usage /ii dev <option>").setStyle(new Style().setColor(TextFormatting.GOLD)));
					sender.sendMessage(getMessageForCommand("slowmo", "toggles bullets slowmo"));
					sender.sendMessage(getMessageForCommand("zawarudo", "toggles bullets slowmo, but it was me, D I O"));
					sender.sendMessage(getMessageForCommand("bulletspeed", "sets bullets slowmo speed", "<0.0-1.0>"));
					sender.sendMessage(getMessageForCommand("killbullets", "removes all bullets in 20 block radius"));
					sender.sendMessage(getMessageForCommand("killvehicles", "removes all vehicles (II multipart entities) in 20 block radius"));
					sender.sendMessage(getMessageForCommand("killitems", "removes all items in 20 block radius"));
					sender.sendMessage(getMessageForCommand("killhanses", "removes all ze Hanses in 20 block radius"));
					sender.sendMessage(getMessageForCommand("world_setup", "disables day and night and weather cycles, disables mob spawning"));
					sender.sendMessage(getMessageForCommand("tpd", "teleports the player to a dimension", "<dim>"));
					sender.sendMessage(getMessageForCommand("decaybullets", "toggles bullets decay (despawning after x amount of ticks existing)"));
					sender.sendMessage(getMessageForCommand("test_enemies", "spawns enemies", "<amount>"));
					sender.sendMessage(getMessageForCommand("explosion", "spawns an II explosion", "<size>"));
					sender.sendMessage(getMessageForCommand("nuke", "plants a seed on ground zero"));
					sender.sendMessage(getMessageForCommand("power", "charges held item or looked entity with IF, absolutely free"));
					sender.sendMessage(getMessageForCommand("tree", "creates a happy little [R E B B U R] tree"));
					sender.sendMessage(getMessageForCommand("parachute", "spawns and mounts the command user on a parachute"));
					sender.sendMessage(getMessageForCommand("deth", "removes the entity player is looking at"));

				}
				break;
				case "deth":
				{
					if(senderEntity==null)
						return;
					float blockReachDistance = 100f;
					Vec3d vec3d = senderEntity.getPositionEyes(0);
					Vec3d vec3d1 = senderEntity.getLook(0);
					Vec3d vec3d2 = vec3d.addVector(vec3d1.x*blockReachDistance, vec3d1.y*blockReachDistance, vec3d1.z*blockReachDistance);

					MultipleRayTracer rayTracer = MultipleRayTracer.volumetricTrace(sender.getEntityWorld(), vec3d, vec3d2, new AxisAlignedBB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5), true, false, true, Collections.singletonList(senderEntity), Collections.emptyList());
					for(RayTraceResult hit : rayTracer.hits)
					{
						if(hit.typeOfHit==Type.ENTITY)
						{
							hit.entityHit.setDead();
							sender.sendMessage(new TextComponentString(hit.entityHit.getDisplayName().getFormattedText()+" is dead, no big surprise."));
							break;
						}
					}

				}
				break;
				case "tree":
				{
					if(senderEntity==null)
						return;

					float blockReachDistance = 100f;
					Vec3d vec3d = senderEntity.getPositionEyes(0);
					Vec3d vec3d1 = senderEntity.getLook(0);
					Vec3d vec3d2 = vec3d.addVector(vec3d1.x*blockReachDistance, vec3d1.y*blockReachDistance, vec3d1.z*blockReachDistance);
					RayTraceResult traceResult = sender.getEntityWorld().rayTraceBlocks(vec3d, vec3d2, false, false, true);
					if(traceResult==null||traceResult.typeOfHit==Type.MISS)
						return;

					IIWorldGen.worldGenRubberTree.generate(sender.getEntityWorld(), Utils.RAND, traceResult.getBlockPos().up());
					sender.sendMessage(new TextComponentString("Adding a happy little tree :)"));

				}
				break;
				case "slowmo":
					EntityBullet.DEV_SLOMO = 0.005f;
					EntityBullet.DEV_DECAY = false;
					sender.sendMessage(new TextComponentString("Slomo activated!"));
					break;
				case "zawarudo":
					if(EntityBullet.DEV_SLOMO==0f)
					{
						sender.sendMessage(new TextComponentString("Toki wo tomatta."));
						EntityBullet.DEV_SLOMO = 1f;
						EntityBullet.DEV_DECAY = true;
					}
					else
					{
						sender.sendMessage(new TextComponentString("Za Warudo! Toki wo tomare!"));
						EntityBullet.DEV_SLOMO = 0f;
						EntityBullet.DEV_DECAY = false;
					}
					break;
				case "bulletspeed":
					if(args.length > 1)
					{
						EntityBullet.DEV_SLOMO = Float.parseFloat(args[1]);
						sender.sendMessage(new TextComponentString("Bullet speed set to "+args[1]));
					}
					else
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED+"Please enter a speed value, default 1, current "+(int)EntityBullet.DEV_SLOMO));
					}

					break;
				case "killbullets":
					sender.getEntityWorld().getEntities(EntityBullet.class, input -> true).forEach(Entity::setDead);
					sender.sendMessage(new TextComponentString("All bullets killed!"));
					break;
				case "killitems":
					sender.getEntityWorld().getEntities(EntityItem.class, input -> (input!=null?input.getPositionVector().distanceTo(sender.getPositionVector()): 25) < 25f).forEach(Entity::setDead);
					sender.getEntityWorld().getEntities(EntityXPOrb.class, input -> (input!=null?input.getPositionVector().distanceTo(sender.getPositionVector()): 25) < 25f).forEach(Entity::setDead);
					sender.getEntityWorld().getEntities(EntityArrow.class, input -> (input!=null?input.getPositionVector().distanceTo(sender.getPositionVector()): 25) < 25f).forEach(Entity::setDead);
					sender.sendMessage(new TextComponentString("Items Killed!"));
					break;
				case "killvehicles":
					sender.getEntityWorld().getEntities(Entity.class, input -> (input instanceof IVehicleMultiPart?input.getPositionVector().distanceTo(sender.getPositionVector()): 25) < 25f).forEach(Entity::setDead);
					sender.sendMessage(new TextComponentString("Vehicles Killed!"));
					break;
				case "killhanses":
					sender.getEntityWorld().getEntities(EntityHans.class, input -> (input!=null?input.getPositionVector().distanceTo(sender.getPositionVector()): 25) < 25f).forEach(Entity::setDead);
					sender.sendMessage(new TextComponentString("All Hanses killed :("));
					break;
				case "decaybullets":
					EntityBullet.DEV_DECAY = !EntityBullet.DEV_DECAY;
					sender.sendMessage(new TextComponentString(String.valueOf(EntityBullet.DEV_DECAY)));
					break;
				case "world_setup":
					server.getEntityWorld().getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
					server.getEntityWorld().getGameRules().setOrCreateGameRule("doWeatherCycle", "false");
					server.getEntityWorld().getGameRules().setOrCreateGameRule("doMobSpawning", "false");
					sender.sendMessage(new TextComponentString("World setup done!"));
					break;
				case "tpd":
					if(args.length > 1&&senderEntity!=null)
					{
						senderEntity.changeDimension(Integer.parseInt(args[1]), new IITeleporter());
						sender.sendMessage(new TextComponentString("Preparing to jump!"));
					}
					break;
				case "power":
				{
					if(senderEntity==null)
						return;

					float blockReachDistance = 6;
					Vec3d vec3d = senderEntity.getPositionEyes(0);
					Vec3d vec3d1 = senderEntity.getLook(0);
					Vec3d vec3d2 = vec3d.addVector(vec3d1.x*blockReachDistance, vec3d1.y*blockReachDistance, vec3d1.z*blockReachDistance);
					MultipleRayTracer rayTracer = MultipleRayTracer.volumetricTrace(sender.getEntityWorld(), vec3d, vec3d2, new AxisAlignedBB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5), true, false, true, Collections.singletonList(senderEntity), Collections.emptyList());
					if(rayTracer.hits.size() > 0)
					{
						Entity entityHit = rayTracer.hits.get(0).entityHit;
						if(entityHit!=null&&entityHit.hasCapability(CapabilityEnergy.ENERGY, null))
						{
							IEnergyStorage capability = entityHit.getCapability(CapabilityEnergy.ENERGY, null);
							if(capability!=null)
							{
								while(capability.getEnergyStored()!=capability.getMaxEnergyStored())
									capability.receiveEnergy(Integer.MAX_VALUE, false);
								return;
							}
						}
					}

					senderEntity.getHeldEquipment().forEach(stack -> {
						if(stack.getItem() instanceof IAdvancedFluidItem)
						{
							IAdvancedFluidItem item = (IAdvancedFluidItem)stack.getItem();
							for(Fluid f : FluidRegistry.getRegisteredFluids().values())
							{
								FluidStack fluidStack = new FluidStack(f, 10000000);
								if(item.allowFluid(stack, fluidStack))
								{
									IFluidHandler capability = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
									if(capability!=null)
									{
										capability.fill(fluidStack, true);
										break;
									}
								}

							}
						}
						if(stack.hasCapability(CapabilityEnergy.ENERGY, null))
							stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(Integer.MAX_VALUE, false);
					});
				}
				break;
				case "explosion":
				case "nuke":
				{
					if(senderEntity==null||server.getEntityWorld().isRemote)
						return;

					float blockReachDistance = 100f;
					Vec3d vec3d = senderEntity.getPositionEyes(0);
					Vec3d vec3d1 = senderEntity.getLook(0);
					Vec3d vec3d2 = vec3d.addVector(vec3d1.x*blockReachDistance, vec3d1.y*blockReachDistance, vec3d1.z*blockReachDistance);
					RayTraceResult traceResult = senderEntity.getEntityWorld().rayTraceBlocks(vec3d, vec3d2, false, false, true);
					if(traceResult==null||traceResult.typeOfHit==Type.MISS)
						return;

					BlockPos pos = traceResult.getBlockPos();

					if(args[0].equals("nuke"))
					{
						ItemStack s2 = IIContent.itemAmmoArtillery.getBulletWithParams("core_brass", "canister", "nuke");
						EntityBullet a = BulletHelper.createBullet(senderEntity.getEntityWorld(), s2, new Vec3d(pos).addVector(0, 2, 0), new Vec3d(0, -1, 0));
						senderEntity.getEntityWorld().spawnEntity(a);
						/*
						EntityAtomicBoom entityAtomicBoom = new EntityAtomicBoom(server.getEntityWorld(), 0.5f);
						entityAtomicBoom.setPosition(pos.getX(), pos.getY()+2, pos.getZ());
						server.getEntityWorld().spawnEntity(entityAtomicBoom);
						 */
						return;
					}
					int num = 0;
					try
					{
						num = Math.abs(Integer.parseInt(args[1]));
					}
					catch(Exception ignored)
					{

					}
					IIExplosion exp = new IIExplosion(server.getEntityWorld(), senderEntity, pos.getX(), pos.getY()+1, pos.getZ(), num, 1f, false, true);
					exp.doExplosionA();
					exp.doExplosionB(true);
				}
				break;
				case "test_enemies":
				{
					if(senderEntity==null||server.getEntityWorld().isRemote)
						return;

					float blockReachDistance = 100f;
					Vec3d vec3d = senderEntity.getPositionEyes(0);
					Vec3d vec3d1 = senderEntity.getLook(0);
					Vec3d vec3d2 = vec3d.addVector(vec3d1.x*blockReachDistance, vec3d1.y*blockReachDistance, vec3d1.z*blockReachDistance);
					RayTraceResult traceResult = senderEntity.getEntityWorld().rayTraceBlocks(vec3d, vec3d2, false, false, true);
					if(traceResult==null||traceResult.typeOfHit==Type.MISS)
						return;

					BlockPos position = traceResult.getBlockPos().up();

					int num = 0;
					try
					{
						num = Math.abs(Integer.parseInt(args[1]));
					}
					catch(Exception ignored)
					{

					}
					if(!server.getEntityWorld().isRemote)
					{
						for(int i = 0; i < num; i++)
						{
							EntityZombie z1 = new EntityZombie(senderEntity.getEntityWorld());
							z1.setArmsRaised(false);
							z1.setAIMoveSpeed(0.125f);
							z1.setPosition(position.getX(), position.getY(), position.getZ());
							z1.setCustomNameTag("Zombie #"+i);
							z1.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(IIContent.itemLightEngineerHelmet));
							senderEntity.getEntityWorld().spawnEntity(z1);
						}
						sender.sendMessage(new TextComponentString("Test enemies summoned!"));
					}

				}
				break;
				case "parachute":
				{
					if(senderEntity!=null)
					{
						EntityParachute para = new EntityParachute(senderEntity.getEntityWorld());
						para.setPosition(senderEntity.posX, senderEntity.posY, senderEntity.posZ);
						senderEntity.getEntityWorld().spawnEntity(para);
						senderEntity.startRiding(para);
					}
				}
				break;
			}

		}
		else
			throw new WrongUsageException(getUsage(sender));
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel()
	{
		return 4;
	}

	/**
	 * Get a list of options for when the user presses the TAB key
	 */
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		return getListOfStringsMatchingLastWord(args, options);
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index==0;
	}

	public ITextComponent getMessageForCommand(String subcommand, String description)
	{
		return getMessageForCommand(subcommand, description, "");
	}

	public ITextComponent getMessageForCommand(String subcommand, String description, String arguments)
	{
		return new TextComponentString("/ii dev ").appendText(subcommand).appendText(arguments.isEmpty()?arguments: (" "+arguments))
				.setStyle(new Style().setColor(TextFormatting.GOLD).setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/ii dev "+subcommand)))
				.appendSibling(new TextComponentString(" - ").appendText(description).setStyle(new Style().setColor(TextFormatting.RESET)));
	}

	private static class IITeleporter implements ITeleporter
	{
		@Override
		public void placeEntity(World world, Entity entity, float yaw)
		{


			entity.moveToBlockPosAndAngles(world.getSpawnPoint(), yaw, entity.rotationPitch);
		}
	}
}
