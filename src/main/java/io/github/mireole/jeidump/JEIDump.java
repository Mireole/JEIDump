package io.github.mireole.jeidump;

import io.github.mireole.Tags;
import mezz.jei.gui.Focus;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]")
public class JEIDump {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);
    public static Pipe PIPE;
    public static Thread PIPE_THREAD;
    private static final BlockingQueue<String> QUEUE = new LinkedBlockingQueue<>();

    public JEIDump() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent event) {

    }

    @Mod.EventHandler
    public static void onServerStarted(FMLServerStartedEvent event) {
        if (DumpCommand.dump()) LOGGER.warn("Dumped recipes");
        else LOGGER.error("Error while dumping recipes");
    }

    @Mod.EventHandler
    public static void preinit(FMLPreInitializationEvent event) {
        DumpCommand command = new DumpCommand();
        ClientCommandHandler.instance.registerCommand(command);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) return;
        if (PIPE != null) PIPE.close();
        PIPE = new Pipe("/run/user/1000/jeidump");
        PIPE_THREAD = new Thread(JEIDump::pipeThread);
        PIPE_THREAD.start();
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) return;
        if (PIPE_THREAD != null) {
            PIPE_THREAD.interrupt();
            try {
                PIPE_THREAD.join();
            } catch (InterruptedException ignored) {

            }
        }
        PIPE_THREAD = null;
        if (PIPE != null) PIPE.close();
        PIPE = null;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (!QUEUE.isEmpty()) {
                handleMessage(QUEUE.poll());
            }
        }
    }

    private static void pipeThread() {
        while (!Thread.interrupted()) {
            String message = PIPE.read();
            if (message == null) return;
            LOGGER.info("Received message: {}", message);
            QUEUE.add(message);
        }
    }

    private static void handleMessage(String message) {
        if (JeiIntegration.recipesGui == null) return;

        String[] parts = message.split(":");

        if (parts.length == 3) {
            // Item
            ResourceLocation location = new ResourceLocation(parts[0], parts[1]);
            int meta = Integer.parseInt(parts[2]);
            Item item = Item.REGISTRY.getObject(location);
            if (item == null) {
                LOGGER.error("Invalid item: {}", location);
                return;
            }
            ItemStack stack = new ItemStack(item, 1, meta);
            JeiIntegration.recipesGui.show(new Focus<>(Focus.Mode.INPUT, stack));
            return;
        }
        else if (parts.length == 1) {
            // Fluid
            Fluid fluid = FluidRegistry.getFluid(parts[0]);
            if (fluid == null) {
                LOGGER.error("Invalid fluid: {}", parts[0]);
                return;
            }
            FluidStack stack = new FluidStack(fluid, 1000);
            JeiIntegration.recipesGui.show(new Focus<>(Focus.Mode.INPUT, stack));
            return;
        }
        LOGGER.error("Invalid message: {}", message);
    }

}
