/*
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.powertac.common;

import java.util.HashMap;

import org.powertac.common.state.Domain;
import org.powertac.common.state.StateChange;

/**
 * A broker instance represents a competition participant.
 * Broker instances are not communicated to other brokers; only usernames are 
 * considered public information and get communicated. Every entity that needs
 * to trade in the wholesale market or post TariffSpecifications must be a
 * broker.
 * <p>
 * Brokers may be local or non-local (remote), and they may be wholesale or
 * non-wholesale (retail) brokers. Remote brokers receive messages through
 * JMS, while local brokers are assumed to reside in the server's process space
 * where they receive messages by calls to their receiveMessage() methods. Local
 * brokers must override receiveMessage() to see these messages, otherwise they
 * will be dropped on the floor. Local brokers can send messages by calling
 * BrokerProxy.routeMessage();</p>
 * <p>
 * Wholesale brokers are not permitted to offer tariffs, but may trade in the
 * wholesale market, and they are not included in the balancing process.</p>
 *
 * @author Carsten Block, David Dauer, John Collins
 */
@Domain
public class Broker 
{
  private long id = IdGenerator.createId();

  /** the broker's login or user name     */
  private String username;

  //private String password;
  private boolean enabled;

  /** If true, the broker is local to the server and does not receive messages  */
  private boolean local = false;
  
  /** If true, broker is a wholesale market participant, but not a "real" broker */
  private boolean wholesale = false;
  
  private HashMap<Integer, MarketPosition> mktPositions;

  //def testProxy = null // redirect incoming messages for testing

  /**
   * Constructor for username only.
   */
  public Broker (String username)
  {
    super();
    this.username = username;
    mktPositions = new HashMap<Integer, MarketPosition>();
  }
  
  /**
   * Constructor to specify non-standard local/wholesale flags.
   */
  public Broker (String username, boolean local, boolean wholesale)
  {
    super();
    this.username = username;
    mktPositions = new HashMap<Integer, MarketPosition>();
    this.local = local;
    this.wholesale = wholesale;
  }
  
  /** Broker's current cash position  */
  private CashPosition cashPosn;

  /**
   * Returns the unique ID for this broker
   */
  public long getId ()
  {
    return id;
  }
  
  /**
   * Returns the unique ID for this broker as a String.
   */
  public String getApiKey() 
  {
    return Long.toString(id);
  }

  /**
   * Returns the CashPosition for this broker, which is either the CashPosition
   * supplied with the most recent call to setCash(), or a dummy CashPosition
   * with a balance of 0.0. The returned value is guaranteed to be non-null.
   */
  public CashPosition getCash() 
  {
    if (cashPosn == null) {
      cashPosn = new CashPosition(this, 0.0);
    }
    return cashPosn;
  }

  /**
   * Updates the current CashPosition for this Broker.
   */
  @StateChange
  public void setCash(CashPosition thing) 
  {
    cashPosn = thing;
  }

  /**
   * Associates a MarketPosition with a given Timeslot. 
   */
  @StateChange
  public Broker addMarketPosition (MarketPosition posn, Timeslot slot)
  {
    mktPositions.put(slot.getSerialNumber(), posn);
    return this;
  }
  
  /**
   * Returns the MarketPosition associated with the given Timeslot. Result will
   * be null if addMarketPosition has never been called for this Timeslot.
   */
  public MarketPosition findMarketPositionByTimeslot (Timeslot slot)
  {
    return mktPositions.get(slot.getSerialNumber());
  }

  /**
   * Returns the username for this Broker.
   */
  public String getUsername ()
  {
    return username;
  }

  //public String getPassword ()
  //{
  //  return password;
  //}

  /**
   * True just in case either the broker is logged in, or is a local wholesale
   * broker.
   */
  public boolean isEnabled ()
  {
    return (enabled || (isLocal() && isWholesale()));
  }
  
  /**
   * Enables this Broker. Of course, calling this method on a remote broker
   * will have no effect; it must be called on the Broker instance in the
   * server.
   */
  public void setEnabled(boolean enabled) 
  {
    this.enabled = enabled;
  }

  /**
   * True for a Broker that is local to the server. Affects message routing.
   */
  public boolean isLocal ()
  {
    return local;
  }
  
  /**
   * Allows subclasses to set themselves as local brokers. Local brokers
   * must subclass this class, and implement receiveMessage() to receive
   * messages from the server. They send messages by calling
   * BrokerProxy.routeMessage(). 
   */
  @StateChange
  protected void setLocal (boolean value)
  {
    local = value;
  }

  /**
   * True for a broker that operates on the wholeside of the wholesale market.
   */
  public boolean isWholesale ()
  {
    return wholesale;
  }
  
  /** Allows subclasses to make themselves wholesale brokers */
  @StateChange
  protected void setWholesale (boolean value)
  {
    wholesale = value;
  }

  @Override
  public String toString() 
  {
    return username;
  }

  public String toQueueName() 
  {
    return ("brokers." + this.username + ".outputQueue");
  }

  /**
   * Default implementation does nothing.
   * Broker subclasses implemented within the server can override this method 
   * to receive messages from BrokerProxy
   */
  public void receiveMessage(Object object) 
  {
  }
}
