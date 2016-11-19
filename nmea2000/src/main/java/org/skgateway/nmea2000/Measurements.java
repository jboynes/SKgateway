/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.skgateway.nmea2000;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;
import javax.measure.spi.SystemOfUnits;

/**
 *
 */
public class Measurements {

    private static final ServiceProvider provider = ServiceProvider.current();
    private static final QuantityFactory<Dimensionless> dimensionlessFactory = provider.getQuantityFactory(Dimensionless.class);
    private static final QuantityFactory<Length> lengthFactory = provider.getQuantityFactory(Length.class);
    private static final QuantityFactory<Speed> speedFactory = provider.getQuantityFactory(Speed.class);
    private static final QuantityFactory<Angle> angleFactory = provider.getQuantityFactory(Angle.class);
    private static final QuantityFactory<Temperature> temperatureFactory = provider.getQuantityFactory(Temperature.class);
    private static final QuantityFactory<Pressure> pressureFactory = provider.getQuantityFactory(Pressure.class);

    private static final SystemOfUnits SI = provider.getSystemOfUnitsService().getSystemOfUnits();

    public static Quantity<Dimensionless> one(Number value) {
        return dimensionlessFactory.create(value, SI.getUnit(Dimensionless.class));
    }

    public static Quantity<Length> length(Number value) {
        return lengthFactory.create(value, SI.getUnit(Length.class));
    }

    public static Quantity<Speed> speed(Number value) {
        return speedFactory.create(value, SI.getUnit(Speed.class));
    }

    public static Quantity<Angle> angle(Number value) {
        return angleFactory.create(value, SI.getUnit(Angle.class));
    }

    public static Quantity<Temperature> temperature(Number value) {
        return temperatureFactory.create(value, SI.getUnit(Temperature.class));
    }

    public static Quantity<Pressure> pressure(Number value) {
        return pressureFactory.create(value, SI.getUnit(Pressure.class));
    }
}
