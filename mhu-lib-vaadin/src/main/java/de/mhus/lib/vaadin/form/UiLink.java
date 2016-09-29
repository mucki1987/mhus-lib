package de.mhus.lib.vaadin.form;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MXml;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.core.util.Rfc1738;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.ComponentAdapter;
import de.mhus.lib.form.ComponentDefinition;
import de.mhus.lib.form.DataSource;
import de.mhus.lib.form.UiComponent;

public class UiLink extends UiVaadin {

	private Object value;



	@Override
	protected void setValue(Object value) throws MException {
		DataSource ds = getForm().getDataSource();
//		((Link)getComponentEditor()).setTargetName("_blank");
//		((Link)getComponentEditor()).setResource(new ExternalResource( MCast.toString(value) ));
//		((Link)getComponentEditor()).setCaption( ds.getString(this, "label", getConfig().getString("label", "label=Link") ) );
		
		String label = MXml.encode( ds.getString(this, "label", getConfig().getString("label", "label=Link") ) );
		((Label)getComponentEditor()).setCaptionAsHtml(true);
		if (value == null)
			((Label)getComponentEditor()).setCaption(label );
		else {
			String link = MXml.encode( String.valueOf( value )  );
			((Label)getComponentEditor()).setCaption("<a href=\"" + link + "\" target=_blank>" + label + "</a>" );
		}
		this.value = value;
	}

	@Override
	public Component createEditor() {
		return new Label();
//		return new Link();
	}

	@Override
	protected Object getValue() throws MException {
		return value;
//		return ((Link)getComponentEditor()).getResource().toString();
	}

	
	
	@Override
	public void setEnabled(boolean enabled) throws MException {
	}



	public static class Adapter implements ComponentAdapter {

		@Override
		public UiComponent createAdapter(IConfig config) {
			return new UiLink();
		}

		@Override
		public ComponentDefinition getDefinition() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
