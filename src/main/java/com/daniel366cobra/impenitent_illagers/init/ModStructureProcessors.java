package com.daniel366cobra.impenitent_illagers.init;

import com.daniel366cobra.impenitent_illagers.structure.processors.MossifyAndErodeProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

public final class ModStructureProcessors {
    public static StructureProcessorType<MossifyAndErodeProcessor> MOSSIFY_AND_ERODE_PROCESSOR = () -> MossifyAndErodeProcessor.CODEC;


    public static void register() {
        Registry.register(Registry.STRUCTURE_PROCESSOR, new Identifier(MOD_ID, "mossify_and_erode_processor"), MOSSIFY_AND_ERODE_PROCESSOR);
    }

}
