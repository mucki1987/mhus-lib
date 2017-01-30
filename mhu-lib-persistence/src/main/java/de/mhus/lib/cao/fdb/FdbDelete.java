package de.mhus.lib.cao.fdb;

import java.io.File;

import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoAspect;
import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.action.CantExecuteException;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.cao.action.DeleteConfiguration;
import de.mhus.lib.cao.aspect.Changes;
import de.mhus.lib.cao.util.DefaultChangesQueue.Change;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.strategy.Monitor;
import de.mhus.lib.core.strategy.NotSuccessful;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;

public class FdbDelete extends CaoAction {

	@Override
	public String getName() {
		return DELETE;
	}

	@Override
	public CaoConfiguration createConfiguration(CaoList list, IProperties configuration) throws CaoException {
		return new DeleteConfiguration(null, list, null);
	}

	@Override
	public boolean canExecute(CaoConfiguration configuration) {
		return configuration.getList().size() > 0
				&&
				core.containsNodes(configuration.getList());
	}

	@Override
	public OperationResult doExecuteInternal(CaoConfiguration configuration, Monitor monitor) throws CaoException {
		if (!canExecute(configuration)) return new NotSuccessful(getName(), "can't execute", -1);
		try {
			boolean deleted = false;
			monitor = checkMonitor(monitor);
			boolean recursive = configuration.getProperties().getBoolean(DeleteConfiguration.RECURSIVE, false);
			monitor.setSteps(configuration.getList().size());
			for (CaoNode item : configuration.getList()) {
				monitor.log().i(">>>",item);
				if (item instanceof FdbNode) {
					monitor.incrementStep();
					FdbNode n = (FdbNode)item;
					if (n.getNodes().size() > 0 && !recursive)
						monitor.log().i("*** Node is not empty",item);
					else {
						monitor.log().d("=== Delete",item);
						File f = n.getFile();
						((FdbCore)core).lock();
						try {
							((FdbCore)core).deleteIndex(n.getString("_id", null));
							MFile.deleteDir(f);
						} finally {
							((FdbCore)core).release();
						}
						deleted = true;
						Changes change = item.adaptTo(Changes.class);
						if (change != null) change.deleted();
					}
				}
				
				monitor.log().i("<<<",item);
			}
			if (deleted)
				return new Successful(getName());
			else
				return new NotSuccessful(getName(), "no nodes deleted", -1);
		} catch (Throwable t) {
			log().w(t);
			return new NotSuccessful(getName(),t.toString(),-1);
		}
	}

}