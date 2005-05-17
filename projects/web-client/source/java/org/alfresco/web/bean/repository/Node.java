package org.alfresco.web.bean.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.apache.log4j.Logger;

/**
 * Lighweight client side representation of a node held in the repository. 
 * 
 * @author gavinc
 */
public class Node implements Serializable
{
   private static final long serialVersionUID = 3544390322739034169L;

   private static Logger logger = Logger.getLogger(Node.class);
   
   private NodeRef nodeRef;
   private String name;
   private ClassRef type;
   private String path;
   private String id;
   private Set aspects = null;
   private Map<String, Object> properties = new HashMap<String, Object>(7, 1.0f);
   
   private boolean propsRetrieved = false;
   private NodeService nodeService;
   
   /**
    * Constructor
    * 
    * @param nodeRef The NodeRef this Node wrapper represents
    * @param nodeService The node service to use to retrieve data for this node 
    */
   public Node(NodeRef nodeRef, NodeService nodeService)
   {
      if (nodeRef == null)
      {
         throw new IllegalArgumentException("NodeRef must be specified for creation of a Node.");
      }
      
      if (nodeService == null)
      {
         throw new IllegalArgumentException("The NodeService must be supplied for creation of a Node.");
      }
      
      this.nodeRef = nodeRef;
      this.id = nodeRef.getId();
      this.nodeService = nodeService;
   }

   /**
    * @return All the properties known about this node.
    */
   public Map<String, Object> getProperties()
   {
      if (this.propsRetrieved == false)
      {
         // TODO: How are we going to deal with namespaces, JSF won't understand so
         //       we will need some sort of mechanism to deal with it????
         //       For now just get the local name of each property.
         
         Map<QName, Serializable> props = this.nodeService.getProperties(this.nodeRef);
         
         for (QName qname: props.keySet())
         {
            String localName = qname.getLocalName();
            this.properties.put(localName, props.get(qname));
         }
         
         this.propsRetrieved = true;
      }
      
      return properties;
   }

   /**
    * @return Returns the NodeRef this Node object represents
    */
   public NodeRef getNodeRef()
   {
      return this.nodeRef;
   }
   
   /**
    * @return Returns the type.
    */
   public ClassRef getType()
   {
      if (this.type == null)
      {
         this.type = this.nodeService.getType(this.nodeRef);
      }
      
      return type;
   }
   
   /**
    * @return Returns the type name as a string.
    */
   public String getTypeName()
   {
      return getType().getQName().getLocalName();
   }
   
   /**
    * @return The display name for the node
    */
   public String getName()
   {
      if (this.name == null)
      {
         // try and get the name from the properties first
         this.name = (String)getProperties().get("name");
         
         // if we didn't find it as a property get the name from the association name
         if (this.name == null)
         {
            this.name = this.nodeService.getPrimaryParent(this.nodeRef).getQName().getLocalName(); 
         }
      }
      
      return this.name;
   }

   /**
    * @return The list of aspects applied to this node
    */
   public Set getAspects()
   {
      if (this.aspects == null)
      {
         this.aspects = this.nodeService.getAspects(this.nodeRef);
      }
      
      return this.aspects;
   }
   
   /**
    * @param aspect The aspect to test for
    * @return true if the node has the aspect false otherwise
    */
   public boolean hasAspect(ClassRef aspect)
   {
      Set aspects = getAspects();
      return aspects.contains(aspect);
   }

   /**
    * @return The GUID for the node
    */
   public String getId()
   {
      return this.id;
   }

   /**
    * @return The path for the node
    */
   public String getPath()
   {
      if (this.path == null)
      {
         this.path = this.nodeService.getPath(this.nodeRef).toString();
      }
      
      return this.path;
   }
   
//   /**
//    * Used to save the properties edited by the user
//    * 
//    * @return The outcome string
//    */
//   public String persist()
//   {
//      logger.debug("Updating properties for: " + this + "; properties = " + this.properties);
//      
//      // TODO: Use whatever service to persist the Node back to the repository
//      
//      return "success";
//   }   
}
