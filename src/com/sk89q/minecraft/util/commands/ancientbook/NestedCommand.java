// $Id$
/*
 * WorldEdit
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.minecraft.util.commands.ancientbook;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates a nested command. Mark methods with this annotation to tell
 * {@link com.sk89q.minecraft.util.commands.ancientbook.CommandsManager} that a method is merely a shell for child
 * commands. Note that the body of a method marked with this annotation
 * will never called. Additionally, not all fields of {@link com.sk89q.minecraft.util.commands.ancientbook.Command} apply
 * when it is used in conjunction with this annotation, although both
 * are still required.
 *
 * @author sk89q
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NestedCommand {
    /**
     * A list of classes with the child commands.
     */
    Class<?>[] value();
}
