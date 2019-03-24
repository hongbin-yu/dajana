package com.filemark.jcr.model;

import java.util.List;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes = "mix:versionable")
public class Djcontainer implements SmartiNode {
	
	@Field(uuid=true)
	protected String uid;
	@Field(path=true)	
	protected String path;
	@Field
	private String tagName;
	@Field
	private String cssClasses;
	@Field
	private String resourceType="djcontainer";
	@Field
	private int maximium = 1;
	@Field
	private String text;
	@Collection(jcrElementName="component")
	private List<Djcontainer> children;
	enum Types {djcontainer,djtext,djtable,djh2,djasset,djcomment,djchat};
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getUid() {
		return uid;
	}


	public void setUid(String uid) {
		this.uid = uid;
	}


	public String getTagName() {
		return tagName;
	}


	public void setTagName(String tagName) {
		this.tagName = tagName;
	}


	public String getCssClasses() {
		return cssClasses;
	}


	public void setCssClasses(String cssClasses) {
		this.cssClasses = cssClasses;
	}


	public String getResourceType() {
		return resourceType;
	}


	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public List<Djcontainer> getChildren() {
		return children;
	}


	public void setChildren(List<Djcontainer> children) {
		this.children = children;
	}
	
	public String getEditHtml() {
		String html="";
		
		if(tagName == null) {
			tagName ="div";
		}
		Types type = Types.valueOf(resourceType);
		switch (type) {
		case djcontainer:
			html += "<"+tagName+" id=\"dropoff\" class=\"component "+(cssClasses==null?resourceType:resourceType+" "+cssClasses)+"\">";
			html+="<div class=\"text-center addcomponent\" ondrop=\"drop(event)\" ondragover=\"allowDrop(event)\"></div>";
			break;
		case djasset:
			break;
		case djchat:
			break;
		case djcomment:
			break;
		case djh2:
			//html += "<"+tagName+" id='"+uid+"' class=editable "+cssClasses==null?"'"+resourceType+"'":resourceType+" "+cssClasses+"'>";
			break;
		case djtable:
			break;
		case djtext:
			//html += "<"+tagName+" id='"+uid+"' class=editable "+cssClasses==null?"'"+resourceType+"'":resourceType+" "+cssClasses+"'>";
			break;
		default:
			//html +=text;
			break;
		}
		if(children!=null)
			for(Djcontainer c:children) {
				//html += c.getEditHtml();
			}
		if(resourceType.equals("djcontainer") && children == null || children.size()<maximium)  {
			html += "<div class=\"component text-center well\" ondrop=\"drop(event)\" ondragover=\"allowDrop(event)\">Drop component here</div>";
		}
		if(tagName != null) {
			html +="</"+tagName+">\r\n";
		}
		return html;
	
	}


	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getType() {
		return Djcontainer.class.getSimpleName().toLowerCase();
	}
}
