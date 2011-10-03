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

import org.powertac.common.state.Domain;

import com.thoughtworks.xstream.annotations.*;

/**
 * Each instance is an individual un-cleared entry (a Bid or an Ask) within an Orderbook.
 * @author Daniel Schnurr
 */
@Domain
@XStreamAlias("orderbook-bid")
public class OrderbookOrder implements Comparable 
{

  @XStreamAsAttribute
  private double limitPrice;

  @XStreamAsAttribute
  private double mWh;
  
  @XStreamAsAttribute
  private Shout.OrderType orderType;
  
  public OrderbookOrder (Shout.OrderType orderType, double limitPrice, double mWh)
  {
    super();
    this.orderType = orderType;
    this.limitPrice = limitPrice;
    this.mWh = mWh;
  }

  public int compareTo(Object o) {
    if (!(o instanceof OrderbookOrder)) 
      return 1;
    OrderbookOrder other = (OrderbookOrder) o;
    return (this.limitPrice == (other.limitPrice) ? 0 : (this.limitPrice < other.limitPrice ? 1 : -1));
  }

  public Shout.OrderType getOrderType ()
  {
    return orderType;
  }
  
  public double getLimitPrice ()
  {
    return limitPrice;
  }

  /**
   * @deprecated Use {@link getMWh()} instead.
   */
  @Deprecated
  public double getQuantity ()
  {
    return mWh;
  }

  public double getMWh ()
  {
    return mWh;
  }
}