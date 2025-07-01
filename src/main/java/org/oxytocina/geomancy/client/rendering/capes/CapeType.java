package org.oxytocina.geomancy.client.rendering.capes;

import net.minecraft.util.Identifier;

import static org.oxytocina.geomancy.Geomancy.locate;

public enum CapeType {
    DEV(locate("textures/capes/dev.png"), true),
    NONE(null, false);

    public final Identifier capePath;
    public final boolean render;

    CapeType(Identifier capePath, boolean render) {
        this.capePath = capePath;
        this.render = render;
    }
}