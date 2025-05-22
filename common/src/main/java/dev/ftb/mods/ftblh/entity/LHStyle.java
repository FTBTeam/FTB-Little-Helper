package dev.ftb.mods.ftblh.entity;

import dev.ftb.mods.ftblh.FTBLittleHelper;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum LHStyle {
    DEFAULT("default", FTBLittleHelper.id("textures/entity/little_helper.png"), true),
    VANILLA("vanilla", new ResourceLocation("textures/entity/allay/allay.png"), false)
    ;

    private static final Map<String, LHStyle> BY_NAME = Util.make(new HashMap<>(),
            map -> Arrays.stream(LHStyle.values()).forEach(skin -> map.put(skin.name, skin))
    );

    private final String name;
    private final ResourceLocation texture;
    private final boolean rightSide;

    LHStyle(String name, ResourceLocation texture, boolean rightSide) {
        this.name = name;
        this.texture = texture;
        this.rightSide = rightSide;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public boolean isRightSide() {
        return rightSide;
    }

    public static Optional<LHStyle> byName(String name) {
        return Optional.ofNullable(BY_NAME.get(name));
    }
}
