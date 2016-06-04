package cz.muni.fi.pv243.spatialtracker.infinispan;

import cz.muni.fi.pv243.spatialtracker.issues.dto.IssueDetailsBrief;
import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.infinispan.Cache;

import java.util.List;

/**
 * @author opontes
 */
public interface CacheProvider {
    Cache<String, IssueDetailsBrief> getIssueCache();

    Cache<String, List<WebChatMessage>> getMessageCache();
}
