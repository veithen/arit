/*
 * Copyright 2010-2011 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.arit.spring;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ModuleTypeBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    @Override
    protected Class<?> getBeanClass(Element element) {
        return ModuleTypeFactory.class;
    }

    @Override
    protected void postProcess(BeanDefinitionBuilder builder, Element element) {
        AbstractBeanDefinition definition = builder.getRawBeanDefinition();
        definition.addQualifier(new AutowireCandidateQualifier(Qualifier.class, definition.getPropertyValues().getPropertyValue("identifier").getValue()));
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        return "module-type:" + definition.getPropertyValues().getPropertyValue("identifier").getValue();
    }
}
