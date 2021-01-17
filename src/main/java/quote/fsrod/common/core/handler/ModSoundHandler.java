package quote.fsrod.common.core.handler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import quote.fsrod.common.lib.LibMisc;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSoundHandler {

    public static final SoundEvent spellWind = makeSoundEvent("spell.wind");
    public static final SoundEvent spellCircle = makeSoundEvent("spell.circle");
    public static final SoundEvent foxFire = makeSoundEvent("fox.fire");
    public static final SoundEvent itemRodSuccess = makeSoundEvent("rod.success");

    private static IForgeRegistry<SoundEvent> registry;

    private static SoundEvent makeSoundEvent(String name){
        ResourceLocation loc = new ResourceLocation(LibMisc.MOD_ID, name);
		return new SoundEvent(loc).setRegistryName(loc);
    }
    
    @SubscribeEvent
    public static void initSounds(RegistryEvent.Register<SoundEvent> event){
        registry = event.getRegistry();

        register(spellWind);
        register(spellCircle);
        register(foxFire);
        register(itemRodSuccess);
    }

    private static void register(SoundEvent sound){
        registry.register(sound);
    }

    private ModSoundHandler(){}
}