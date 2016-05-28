package cz.muni.fi.pv243.spatialtracker.webchat.store;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.infinispan.Cache;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commons.CacheConfigurationException;
import org.infinispan.commons.CacheException;
import org.infinispan.commons.api.AsyncCache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.configuration.Builder;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.logging.Log;
import org.infinispan.commons.logging.LogFactory;
import org.infinispan.commons.marshall.Marshaller;
import org.infinispan.commons.util.Util;
import org.infinispan.configuration.ConfigurationManager;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.container.DataContainer;
import org.infinispan.distribution.ch.ConsistentHashFactory;
import org.infinispan.eviction.EvictionType;
import org.infinispan.factories.GlobalComponentRegistry;
import org.infinispan.factories.components.ComponentMetadataRepo;
import org.infinispan.globalstate.ScopedPersistentState;
import org.infinispan.jmx.PlatformMBeanServerLookup;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.marshall.core.VersionAwareMarshaller;
import org.infinispan.notifications.Listenable;
import org.infinispan.persistence.spi.PersistenceException;
import org.infinispan.persistence.support.SingletonCacheWriter;
import org.infinispan.remoting.ReplicationQueue;
import org.infinispan.remoting.transport.Address;
import org.infinispan.remoting.transport.jgroups.SuspectException;
import org.infinispan.security.SecureCache;
import org.infinispan.stats.CacheContainerStats;
import org.infinispan.transaction.LockingMode;
import org.infinispan.util.CyclicDependencyException;
import org.infinispan.util.concurrent.IsolationLevel;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.marshalling.ByteOutput;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

import cz.muni.fi.pv243.spatialtracker.webchat.model.WebChatMessage;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(Arquillian.class)
public class WebChatMessageStoreTest {

	@Deployment
	public static JavaArchive createDeployment() {
		return ShrinkWrap.create(JavaArchive.class)
				.addClasses(WebChatMessageStore.class, TestCacheContainerProvider.class, CacheContainerProvider.class,
						WebChatMessage.class, BasicCache.class, org.infinispan.commons.logging.LogFactory.class)
				.addPackages(false, AsyncCache.class.getPackage(), GlobalConfigurationBuilder.class.getPackage(), ConfigurationBuilder.class.getPackage(),
						CacheConfigurationException.class.getPackage(), Util.class.getPackage(), Log.class.getPackage(),
						Builder.class.getPackage(), org.infinispan.util.logging.LogFactory.class.getPackage(),
						Cache.class.getPackage(), CacheException.class.getPackage(), Address.class.getPackage(), EmbeddedCacheManager.class.getPackage(), Listenable.class.getPackage(),
						PersistenceException.class.getPackage(),
						SingletonCacheWriter.class.getPackage(), SuspectException.class.getPackage(),
						org.infinispan.commons.configuration.attributes.AttributeInitializer.class.getPackage(),
						PlatformMBeanServerLookup.class.getPackage(), Marshaller.class.getPackage(),
						VersionAwareMarshaller.class.getPackage(), ByteBuffer.class.getPackage(),
						ByteOutput.class.getPackage(), GlobalComponentRegistry.class.getPackage(), ComponentStatus.class.getPackage(),
						ComponentMetadataRepo.class.getPackage())
				.addPackages(true, CacheException.class.getPackage(), SecureCache.class.getPackage(), ReplicationQueue.class.getPackage(),
						ConsistentHashFactory.class.getPackage(), ScopedPersistentState.class.getPackage(), DataContainer.class.getPackage(),
						EvictionType.class.getPackage(), IsolationLevel.class.getPackage(), LockingMode.class.getPackage(),
						ReplicableCommand.class.getPackage(), CyclicDependencyException.class.getPackage(), CacheContainerStats.class.getPackage(),
						ConfigurationManager.class.getPackage())
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Inject
	WebChatMessageStore store;

	@Test
	public void addMessageTest() {
		addSampleKeyMessages();

		assertSampleKeyMessages();
	}

	//@Test
	public void getMessagesTest() {
		List<WebChatMessage> msgs = store.getMessages(sampleKey);

		assertEquals(msgs.size(), 0);
	}

	//@Test
	public void addMessagesWithDifferentKeyTest() {
		addSampleKeyMessages();
		addDifferentKeyMessages();

		assertSampleKeyMessages();
		assertDifferentKeyMessages();
	}

	private final String sampleKey = "someKey";

	private final String sampleName1 = "John Doe";
	private final String sampleText1 = "first message";

	private final String sampleName2 = "Second John Doe";
	private final String sampleText2 = "second message";

	private void addSampleKeyMessages() {
		store.addMessage(sampleKey, new WebChatMessage(sampleName1, sampleText1, new Date()));
		store.addMessage(sampleKey, new WebChatMessage(sampleName2, sampleText2, new Date()));
	}

	private void assertSampleKeyMessages() {
		List<WebChatMessage> msgs = store.getMessages(sampleKey);

		assertEquals(msgs.size(), 2);

		WebChatMessage msg = msgs.get(0);
		assertEquals(msg.name(), sampleName1);
		assertEquals(msg.text(), sampleText1);

		msg = msgs.get(1);
		assertEquals(msg.name(), sampleName2);
		assertEquals(msg.text(), sampleText2);
	}

	private final String differentKey = "different key";

	private final String differentName = "James Bond";
	private final String differentText = "different text";

	private void addDifferentKeyMessages() {
		store.addMessage(differentKey, new WebChatMessage(differentName, differentText, new Date()));
	}

	private void assertDifferentKeyMessages() {
		List<WebChatMessage> msgs = store.getMessages(differentKey);

		assertEquals(msgs.size(), 1);

		WebChatMessage msg = msgs.get(0);
		assertEquals(msg.name(), differentName);
		assertEquals(msg.text(), differentText);
	}
}
