package com.github.tianjing.tgtools.flowable.autoconfigure.service;

import org.flowable.ui.common.model.RemoteGroup;
import org.flowable.ui.common.model.RemoteToken;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 田径
 * @create 2019-07-30 9:27
 * @desc
 **/
@Service
public class IdmRemoteIdmService implements RemoteIdmService {
    @Override
    public RemoteUser authenticateUser(String s, String s1) {
        return null;
    }

    @Override
    public RemoteToken getToken(String s) {
        return null;
    }

    @Override
    public RemoteUser getUser(String s) {
        return null;
    }

    @Override
    public List<RemoteUser> findUsersByNameFilter(String s) {
        return null;
    }

    @Override
    public List<RemoteUser> findUsersByGroup(String s) {
        return null;
    }

    @Override
    public RemoteGroup getGroup(String s) {
        return null;
    }

    @Override
    public List<RemoteGroup> findGroupsByNameFilter(String s) {
        return null;
    }
}
