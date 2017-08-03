/*
 * #%L
 * Cantharella :: Service
 * $Id: NormalizerAspect.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/main/java/nc/ird/cantharella/service/utils/normalizers/utils/NormalizerAspect.java $
 * %%
 * Copyright (C) 2009 - 2012 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package nc.ird.cantharella.service.utils.normalizers.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Normalizer aspect, automatically normalizes data by wrapping methods with Normalize annotation on its parameters
 * 
 * @author Mickael Tricot
 * @author Adrien Cheype
 */
@Aspect
@Component
public final class NormalizerAspect {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(NormalizerAspect.class);

    /**
     * Automatically normalizes data by wrapping methods with Normalize annotation on its parameters
     * 
     * @param joinPoint Joint point
     * @return Return of the method
     * @throws Throwable Throwable
     */
    @SuppressWarnings("unchecked")
    @Around("execution(* nc.ird.cantharella.service.services.*Service.*(..,@Normalize (*),..))")
    public Object normalize(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // LOG.debug(method.getDeclaringClass().getSimpleName() + "." + method.getName() + "("
        // + Arrays.asList(method.getParameterTypes()) + ")");
        Object[] args = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; ++i) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Normalize) {
                    args[i] = Normalizer.normalize((Class<Normalizer<Object>>) ((Normalize) annotation).value(),
                            args[i]);
                    LOG.debug("normalize : '" + args[i] + "' in " + method.getDeclaringClass().getSimpleName() + "."
                            + method.getName() + "(" + Arrays.asList(method.getParameterTypes()) + ")");
                }
            }
        }
        return joinPoint.proceed(args);
    }
}
