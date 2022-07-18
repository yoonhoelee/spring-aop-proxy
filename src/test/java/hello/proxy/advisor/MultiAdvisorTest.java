package hello.proxy.advisor;

import hello.proxy.common.service.ServiceInterface;
import hello.proxy.common.service.ServiceInterfaceImpl;
import hello.proxy.proxy.common.advice.TimeAdvice;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class MultiAdvisorTest {
    @Test
    @DisplayName("여러 프록시")
    void multiAdvisorTest1(){
        // client->proxy2(advisor2)-> proxy1(advisor1) -> target

        //make proxy1
        ServiceInterface target = new ServiceInterfaceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        proxyFactory1.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface)proxyFactory1.getProxy();

        // make proxy2, target -> proxy 1 input
        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        proxyFactory2.addAdvisor(advisor2);
        ServiceInterface proxy2 = (ServiceInterface)proxyFactory2.getProxy();

        proxy2.save();

    }
    @Test
    @DisplayName("one proxy multi advisor")
    void multiAdvisorTest2(){
        // client->proxy(advisor2->advisor1)-> target

        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        ServiceInterfaceImpl target = new ServiceInterfaceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        proxyFactory1.addAdvisor(advisor2);
        proxyFactory1.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface)proxyFactory1.getProxy();
        proxy1.save();

    }
    @Slf4j
    static class Advice1 implements MethodInterceptor{
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice 1 call");
            return invocation.proceed();
        }
    }
    @Slf4j
    static class Advice2 implements MethodInterceptor{
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice 2 call");
            return invocation.proceed();
        }
    }
}
