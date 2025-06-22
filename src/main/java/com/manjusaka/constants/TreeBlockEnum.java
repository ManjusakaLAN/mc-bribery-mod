package com.manjusaka.constants;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum TreeBlockEnum {
    OAK_LOG("minecraft:oak_log", "橡木原木"),
    OAK_WOOD("minecraft:oak_wood", "橡木"),
    STRIPPED_OAK_LOG("minecraft:stripped_oak_log", "去皮橡木原木"),
    STRIPPED_OAK_WOOD("minecraft:stripped_oak_wood", "去皮橡木"),

    SPRUCE_LOG("minecraft:spruce_log", "云杉原木"),
    SPRUCE_WOOD("minecraft:spruce_wood", "云杉木"),
    STRIPPED_SPRUCE_LOG("minecraft:stripped_spruce_log", "去皮云杉原木"),
    STRIPPED_SPRUCE_WOOD("minecraft:stripped_spruce_wood", "去皮云杉木"),

    BIRCH_LOG("minecraft:birch_log", "白桦原木"),
    BIRCH_WOOD("minecraft:birch_wood", "白桦木"),
    STRIPPED_BIRCH_LOG("minecraft:stripped_birch_log", "去皮白桦原木"),
    STRIPPED_BIRCH_WOOD("minecraft:stripped_birch_wood", "去皮白桦木"),

    JUNGLE_LOG("minecraft:jungle_log", "丛林原木"),
    JUNGLE_WOOD("minecraft:jungle_wood", "丛林木"),
    STRIPPED_JUNGLE_LOG("minecraft:stripped_jungle_log", "去皮丛林原木"),
    STRIPPED_JUNGLE_WOOD("minecraft:stripped_jungle_wood", "去皮丛林木"),

    ACACIA_LOG("minecraft:acacia_log", "金合欢原木"),
    ACACIA_WOOD("minecraft:acacia_wood", "金合欢木"),
    STRIPPED_ACACIA_LOG("minecraft:stripped_acacia_log", "去皮金合欢原木"),
    STRIPPED_ACACIA_WOOD("minecraft:stripped_acacia_wood", "去皮金合欢木"),

    DARK_OAK_LOG("minecraft:dark_oak_log", "深色橡木原木"),
    DARK_OAK_WOOD("minecraft:dark_oak_wood", "深色橡木"),
    STRIPPED_DARK_OAK_LOG("minecraft:stripped_dark_oak_log", "去皮深色橡木原木"),
    STRIPPED_DARK_OAK_WOOD("minecraft:stripped_dark_oak_wood", "去皮深色橡木木"),

    MANGROVE_LOG("minecraft:mangrove_log", "红树原木"),
    MANGROVE_WOOD("minecraft:mangrove_wood", "红树木"),
    STRIPPED_MANGROVE_LOG("minecraft:stripped_mangrove_log", "去皮红树原木"),
    STRIPPED_MANGROVE_WOOD("minecraft:stripped_mangrove_wood", "去皮红树木"),

    CHERRY_LOG("minecraft:cherry_log", "樱花原木"),
    CHERRY_WOOD("minecraft:cherry_wood", "樱花木"),
    STRIPPED_CHERRY_LOG("minecraft:stripped_cherry_log", "去皮樱花原木"),
    STRIPPED_CHERRY_WOOD("minecraft:stripped_cherry_wood", "去皮樱花木");

    private final String registryId;
    private final String chineseName;

    TreeBlockEnum(String registryId, String chineseName) {
        this.registryId = registryId;
        this.chineseName = chineseName;
    }

    public String getRegistryId() {
        return registryId;
    }

    public String getChineseName() {
        return chineseName;
    }

    public static List<String> getAllTreeBlocks() {
        return Arrays.stream(values()).map(TreeBlockEnum::getRegistryId).toList();
    }

    public static TreeBlockEnum fromRegistryId(String registryId) {
        for (TreeBlockEnum tree : values()) {
            if (Objects.equals(tree.getRegistryId(), registryId)) {
                return tree;
            }
        }
        throw new IllegalArgumentException("未知的树种注册ID：" + registryId);
    }
}
