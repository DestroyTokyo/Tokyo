package delta.cion.cherry.server.motd;

import net.kyori.adventure.text.Component;
import net.minestom.server.ping.ResponseData;

public class ServerMOTD {

	private final ResponseData RESPONSE;

	public ServerMOTD() {
		this.RESPONSE = new ResponseData();
	}

	public ServerMOTD setMOTDOnline(int i) {
		this.RESPONSE.setOnline(i);
		return this;
	}

	public ServerMOTD setMOTDMaxPlayer(int i) {
		this.RESPONSE.setMaxPlayer(i);
		return this;
	}

	public ServerMOTD setMOTDVersion(String s) {
		this.RESPONSE.setVersion(s);
		return this;
	}

	@SuppressWarnings("deprecation")
	public ServerMOTD setMOTDDescription(String s) {
		this.RESPONSE.setDescription(s);
		return this;
	}

	public ServerMOTD setMOTDDescription(Component c) {
		this.RESPONSE.setDescription(c);
		return this;
	}

	public ServerMOTD setMOTDFavicon(String s) {
		this.RESPONSE.setFavicon(s);
		return this;
	}

	public ResponseData get() {
		return this.RESPONSE;
	}

}
