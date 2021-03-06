/*
 * Copyright (c) 2012 by the original author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powertac.common.config;

import static org.junit.Assert.*;

import java.util.TreeMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.powertac.common.Competition;

/**
 * Test for PowerTAC Configurator
 * @author John Collins
 */
public class ConfiguratorTest
{
  Competition comp;
  Configuration config;
  
  /**
   *
   */
  @Before
  public void setUp () throws Exception
  {
    comp = Competition.newInstance("test");
    TreeMap<String, String> map = new TreeMap<String, String>();
    map.put("common.competition.timeslotLength", "15");
    map.put("common.competition.minimumTimeslotCount", "600");
    map.put("common.competition.simulationBaseTime", "2009-10-10");
    config = new MapConfiguration(map);
  }

  @Test
  public void testTimeslotLength ()
  {
    Configurator uut = new Configurator();
    uut.setConfiguration(config);
    uut.configureSingleton(comp);
    assertEquals("correct timeslot length", 15, comp.getTimeslotLength());
    assertEquals("correct min ts count", 600, comp.getMinimumTimeslotCount());
    Instant inst = new DateTime(2009, 10, 10, 0, 0, 0, 0, DateTimeZone.UTC).toInstant();
    assertEquals("correct base time", inst, comp.getSimulationBaseTime());
  }

  @Test
  public void testNoInit ()
  {
    Configurator uut = new Configurator();
    //uut.setConfiguration(config);
    uut.configureSingleton(comp);
    assertEquals("correct timeslot length", 60, comp.getTimeslotLength());
    assertEquals("correct min ts count", 480, comp.getMinimumTimeslotCount());    
  }
}
