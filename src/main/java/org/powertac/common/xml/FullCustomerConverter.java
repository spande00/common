/*
 * Copyright (c) 2011 by the original author
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
package org.powertac.common.xml;

import org.powertac.common.CustomerInfo;
import org.powertac.common.repo.CustomerRepo;
import org.powertac.common.spring.SpringApplicationContext;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FullCustomerConverter implements Converter
{
  CustomerRepo customerRepo;
  
  public FullCustomerConverter ()
  {
    super();
    customerRepo = (CustomerRepo) SpringApplicationContext.getBean("customerRepo");
  }
  
  @SuppressWarnings("rawtypes")
  public boolean canConvert (Class type)
  {
    return CustomerInfo.class.isAssignableFrom(type);
  }

  @Override
  public void marshal (Object source, HierarchicalStreamWriter writer,
                       MarshallingContext context)
  {
    CustomerInfo ci = (CustomerInfo)source;
    customerRepo.add(ci);
    context.convertAnother(ci);
  }

  @Override
  public Object unmarshal (HierarchicalStreamReader reader,
                           UnmarshallingContext context)
  {
    return context.convertAnother(null, CustomerInfo.class);
  }
}