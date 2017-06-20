package com.mindflow.framework.rpc.config.springsupport;

import com.mindflow.framework.rpc.common.URLParamName;
import com.mindflow.framework.rpc.config.ProtocolConfig;
import com.mindflow.framework.rpc.config.ReferenceConfig;
import com.mindflow.framework.rpc.config.RegistryConfig;
import com.mindflow.framework.rpc.util.CollectionUtil;
import com.mindflow.framework.rpc.util.HRpcUtils;
import com.mindflow.framework.rpc.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ReferenceConfigBean<T> extends ReferenceConfig<T> implements
        FactoryBean<T>, BeanFactoryAware,
        InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private transient BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public T getObject() throws Exception {
        return get();
    }

    @Override
    public Class<?> getObjectType() {
        return getInterfaceClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        logger.debug("check reference interface:%s config", getInterfaceName());
        //检查依赖的配置
        checkProtocolConfig();
        checkRegistryConfig();

        if(StringUtils.isEmpty(getGroup())) {
            setGroup(URLParamName.group.getValue());
        }
        if(StringUtils.isEmpty(getVersion())) {
            setVersion(URLParamName.version.getValue());
        }

        if(getTimeout()==null) {
            setTimeout(URLParamName.requestTimeout.getIntValue());
        }
        if(getRetries()==null) {
            setRetries(URLParamName.retries.getIntValue());
        }
    }

    @Override
    public void destroy() throws Exception {
        super.destroy0();
    }

    private void checkRegistryConfig() {
        if (CollectionUtil.isEmpty(getRegistries())) {
            for (String name : HRpcNamespaceHandler.registryDefineNames) {
                RegistryConfig rc = beanFactory.getBean(name, RegistryConfig.class);
                if (rc == null) {
                    continue;
                }
                if (HRpcNamespaceHandler.registryDefineNames.size() == 1) {
                    setRegistry(rc);
                } else if (rc.isDefault() != null && rc.isDefault().booleanValue()) {
                    setRegistry(rc);
                }
            }
        }
        if (CollectionUtil.isEmpty(getRegistries())) {
            setRegistry(HRpcUtils.getDefaultRegistryConfig());
        }
    }

    private void checkProtocolConfig() {
        if (CollectionUtil.isEmpty(getProtocols())) {
            for (String name : HRpcNamespaceHandler.protocolDefineNames) {
                ProtocolConfig pc = beanFactory.getBean(name, ProtocolConfig.class);
                if (pc == null) {
                    continue;
                }
                if (HRpcNamespaceHandler.protocolDefineNames.size() == 1) {
                    setProtocol(pc);
                } else if (pc.isDefault() != null && pc.isDefault().booleanValue()) {
                    setProtocol(pc);
                }
            }
        }
        if (CollectionUtil.isEmpty(getProtocols())) {
            setProtocol(HRpcUtils.getDefaultProtocolConfig());
        }
    }
}
