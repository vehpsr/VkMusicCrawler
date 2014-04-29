package com.gans.vk.data;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

public class GroupInfo {

     private String _groupId;
     private int _groupMembers;

     public boolean isValid() {
         return StringUtils.isNotEmpty(_groupId) && _groupMembers > 0;
     }

     public String getGroupId() {
        return _groupId;
    }

    public void setGroupId(String groupId) {
        _groupId = groupId;
    }

    public int getGroupMembers() {
        return _groupMembers;
    }

    public void setGroupMembers(int groupMembers) {
        _groupMembers = groupMembers;
    }

    @Override
     public String toString() {
         return MessageFormat.format("[{0},{1}]", _groupId, _groupMembers);
     }
}
