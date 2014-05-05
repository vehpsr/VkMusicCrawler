package com.gans.vk.id.statistic;

import com.gans.vk.data.GroupInfo;

public class GroupStatistics {

    private GroupInfo _groupInfo;
    private int _closedAudioPagesCount = 0;
    private int _notEnoughAudioCount = 0;
    private int _parseIdErrorCount = 0;

    public GroupStatistics(GroupInfo groupInfo) {
        _groupInfo = groupInfo;
    }

    public void closedPage() {
        _closedAudioPagesCount++;
    }

    public void notEnoughAudio() {
        _notEnoughAudioCount++;
    }

    public void parserError() {
        _parseIdErrorCount++;
    }

    public String getGroupStatistics() {
        if (_groupInfo == null) {
            return "Invalid group: null";
        }

        StringBuilder result = new StringBuilder();
        result.append("In group: ");
        result.append(_groupInfo.toString()).append("\n");
        result.append(" - closed accounts: ").append(_closedAudioPagesCount).append("\n");
        result.append(" - not enough audio: ").append(_notEnoughAudioCount).append("\n");
        result.append(" - parsing failures: ").append(_parseIdErrorCount);
        return result.toString();
    }

    @Override
    public String toString() {
        return getGroupStatistics();
    }
}
