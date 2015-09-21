package de.mhus.lib.karaf.jms;

import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.Action;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;

@Command(scope = "jms", name = "channel-reset", description = "Reset channels")
public class CmdChannelReset implements Action {

	@Argument(index=0, name="name", required=true, description="ID of the channel or * for all", multiValued=false)
    String name;

	@Override
	public Object execute(CommandSession session) throws Exception {

		JmsManagerService service = JmsUtil.getService();
		if (service == null) {
			System.out.println("Service not found");
			return null;
		}

		if (name == null || name.equals("*"))
			for (String cName : service.listChannels()) {
				try {
					System.out.println(cName);
					JmsDataChannel c = service.getChannel(cName);
					c.reset();
					if (c.getChannel() != null) {
						c.getChannel().reset();
						c.getChannel().open();
					}else
						System.out.println("... channel is null");
				} catch (Throwable t) {
					System.out.println(t);
				}
			}
		else {
			JmsDataChannel channel = service.getChannel(name);
			channel.getChannel().reset();
			channel.getChannel().open();
		}
		System.out.println("OK");
		return null;
	}

}
