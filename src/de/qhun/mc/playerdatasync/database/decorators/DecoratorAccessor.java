/*
 * Copyright (C) 2018 Wrath
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.qhun.mc.playerdatasync.database.decorators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * a handy class to access decorator data
 *
 * @author Wrath
 * @param <Decorator>
 */
public class DecoratorAccessor<Decorator extends Annotation> {

    public Decorator decorator;
    public Field field;
}
