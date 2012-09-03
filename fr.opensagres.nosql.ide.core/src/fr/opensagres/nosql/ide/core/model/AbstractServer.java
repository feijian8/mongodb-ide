package fr.opensagres.nosql.ide.core.model;

import java.io.IOException;
import java.io.Writer;

import fr.opensagres.nosql.ide.core.IServerListener;
import fr.opensagres.nosql.ide.core.Platform;
import fr.opensagres.nosql.ide.core.ServerEvent;
import fr.opensagres.nosql.ide.core.extensions.IServerType;
import fr.opensagres.nosql.ide.core.internal.ServerNotificationManager;
import fr.opensagres.nosql.ide.core.internal.Trace;
import fr.opensagres.nosql.ide.core.settings.ServersConstants;
import fr.opensagres.nosql.ide.core.utils.XMLUtils;

public abstract class AbstractServer extends TreeContainerNode<IServer>
		implements IServer {

	private final String id;
	private String name;;
	private ServerState serverState;
	private ServerNotificationManager notificationManager;
	private IServerRuntime runtime;
	private IServerType serverType;

	public AbstractServer(String serverTypeId, String name) {
		this(serverTypeId, String.valueOf(System.currentTimeMillis()), name);
	}

	public AbstractServer(String serverTypeId, String id, String name) {
		this.id = id;
		this.name = name;
		this.serverState = ServerState.Stopped;
		serverType = Platform.getServerTypeRegistry().getType(serverTypeId);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isConnected() {
		return serverState == ServerState.Started
				|| serverState == ServerState.Connected;
	}

	public ServerState getServerState() {
		return serverState;
	}

	/**
	 * Adds the given server state listener to this server. Once registered, a
	 * listener starts receiving notification of state changes to this server.
	 * The listener continues to receive notifications until it is removed. Has
	 * no effect if an identical listener is already registered.
	 * 
	 * @param listener
	 *            the server listener
	 * @see #removeServerListener(IServerListener)
	 */
	public void addServerListener(IServerListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Module cannot be null");
		if (Trace.LISTENERS) {
			Trace.trace(Trace.STRING_LISTENERS, "Adding server listener "
					+ listener + " to " + this);
		}
		getServerNotificationManager().addListener(listener);
	}

	/**
	 * Adds the given server state listener to this server. Once registered, a
	 * listener starts receiving notification of state changes to this server.
	 * The listener continues to receive notifications until it is removed. Has
	 * no effect if an identical listener is already registered.
	 * 
	 * @param listener
	 *            the server listener
	 * @param eventMask
	 *            the bit-wise OR of all event types of interest to the listener
	 * @see #removeServerListener(IServerListener)
	 */
	public void addServerListener(IServerListener listener, int eventMask) {
		if (listener == null)
			throw new IllegalArgumentException("Module cannot be null");
		if (Trace.LISTENERS) {
			Trace.trace(Trace.STRING_LISTENERS, "Adding server listener "
					+ listener + " to " + this + " with eventMask " + eventMask);
		}
		getServerNotificationManager().addListener(listener, eventMask);
	}

	/**
	 * Removes the given server state listener from this server. Has no effect
	 * if the listener is not registered.
	 * 
	 * @param listener
	 *            the listener
	 * @see #addServerListener(IServerListener)
	 */
	public void removeServerListener(IServerListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Module cannot be null");
		if (Trace.LISTENERS) {
			Trace.trace(Trace.STRING_LISTENERS, "Removing server listener "
					+ listener + " from " + this);
		}
		getServerNotificationManager().removeListener(listener);
	}

	/**
	 * Fire a server listener state change event.
	 */
	protected void fireServerStateChangeEvent() {
		if (Trace.LISTENERS) {
			Trace.trace(Trace.STRING_LISTENERS,
					"->- Firing server state change event: " + getName() + ", "
							+ getServerState() + " ->-");
		}

		if (notificationManager == null || notificationManager.hasNoListeners())
			return;

		getServerNotificationManager().broadcastChange(
				new ServerEvent(ServerEvent.SERVER_CHANGE
						| ServerEvent.STATE_CHANGE, this, getServerState()));
	}

	/**
	 * Fire a server listener saved change event.
	 */
	protected void fireServerSavedChangeEvent() {
		if (Trace.LISTENERS) {
			Trace.trace(Trace.STRING_LISTENERS,
					"->- Firing server state change event: " + getName() + ", "
							+ getServerState() + " ->-");
		}

		if (notificationManager == null || notificationManager.hasNoListeners())
			return;

		getServerNotificationManager().broadcastChange(
				new ServerEvent(ServerEvent.SERVER_CHANGE
						| ServerEvent.SERVER_SAVED, this, getServerState()));
	}

	/**
	 * Fire a server listener saved change event.
	 */
	protected void fireDatabaseCreatedChangeEvent(IDatabase database) {
		if (Trace.LISTENERS) {
			Trace.trace(
					Trace.STRING_LISTENERS,
					"->- Firing database created change event: "
							+ database.getName() + ", " + getServerState()
							+ " ->-");
		}

		if (notificationManager == null || notificationManager.hasNoListeners())
			return;

		getServerNotificationManager().broadcastChange(
				new ServerEvent(ServerEvent.DATABASE_CHANGE
						| ServerEvent.DATABASE_CREATED, database));
	}

	protected void fireDatabaseDroppedChangeEvent(IDatabase database) {
		if (Trace.LISTENERS) {
			Trace.trace(
					Trace.STRING_LISTENERS,
					"->- Firing database dropped change event: "
							+ database.getName() + ", " + getServerState()
							+ " ->-");
		}

		if (notificationManager == null || notificationManager.hasNoListeners())
			return;

		getServerNotificationManager().broadcastChange(
				new ServerEvent(ServerEvent.DATABASE_CHANGE
						| ServerEvent.DATABASE_DROPPED, database));
	}

	/**
	 * Returns the event notification manager.
	 * 
	 * @return the notification manager
	 */
	private ServerNotificationManager getServerNotificationManager() {
		if (notificationManager == null)
			notificationManager = new ServerNotificationManager();

		return notificationManager;
	}

	public final int getType() {
		return NodeTypeConstants.Server;
	}

	public void setRuntime(IServerRuntime runtime) {
		this.runtime = runtime;
	}

	public IServerRuntime getRuntime() {
		return runtime;
	}

	public IServerType getServerType() {
		return serverType;
	}

	public final void save(Writer writer) throws IOException {
		writer.append("<");
		writer.append(ServersConstants.SERVER_ELT);
		// @id attribute
		XMLUtils.writeAttr(ServersConstants.ID_ATTR, this.getId(), writer);
		// @name attribute
		XMLUtils.writeAttr(ServersConstants.NAME_ATTR, this.getName(), writer);
		// @type-id attribute
		XMLUtils.writeAttr(ServersConstants.SERVER_TYPE_ID_ATTR, this
				.getServerType().getId(), writer);
		XMLUtils.writeAttr(ServersConstants.URL_ATTR, this.getURL(), writer);
		// Specific settings
		doSave(writer);

		if (this.getRuntime() != null) {
			XMLUtils.writeAttr(ServersConstants.RUNTIME_ID_ATTR, this
					.getRuntime().getId(), writer);
		}
		writer.append("/>");
		fireServerSavedChangeEvent();
	}

	protected void doSave(Writer writer) throws IOException {

	}

}
