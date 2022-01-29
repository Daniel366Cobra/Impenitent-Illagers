package com.daniel366cobra.impenitent_illagers.init;

import com.daniel366cobra.impenitent_illagers.structure.PillagerFortGenerator;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

public class ModStructurePieces {
    public static final StructurePieceType PILLAGER_FORT_PIECE = PillagerFortGenerator.Piece::new;

    public static void register() {
        Registry.register(Registry.STRUCTURE_PIECE, new Identifier(MOD_ID, "pillager_fort_piece"), PILLAGER_FORT_PIECE);
    }
}
