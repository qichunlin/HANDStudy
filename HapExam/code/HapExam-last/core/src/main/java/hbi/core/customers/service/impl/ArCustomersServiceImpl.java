package hbi.core.customers.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import hbi.core.customers.dto.ArCustomers;
import hbi.core.customers.service.IArCustomersService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ArCustomersServiceImpl extends BaseServiceImpl<ArCustomers> implements IArCustomersService{

}