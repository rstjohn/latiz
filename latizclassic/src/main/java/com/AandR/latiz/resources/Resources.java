package com.AandR.latiz.resources;

import javax.swing.ImageIcon;

public class Resources {

    /**
     * Create an Icon with the path pointing to filename in the resources package.
     * @param path
     * @return
     */
    public static ImageIcon createIcon(String path) {
        return new ImageIcon(Resources.class.getResource(path));
    }
}
