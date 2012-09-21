package hce.core.common.generic

import data_types.basic.DvIdentifier
// TEST: para serializar DataValue a string y ahorrar joins
import com.thoughtworks.xstream.XStream
import data_types.basic.DataValue

class PartyIdentified extends PartyProxy {
    
    String name
    List<DvIdentifier> identifiers
    //static hasMany = [identifiers: DvIdentifier]
    String codedIdentifiers // Las colecciones de datatypes tambien se deben serializar
    
    static transients = ['identifiers', 'externalRef'] // externalRef es de party proxy
    
    def PartyIdentified()
    {
       identifiers = []
    }
    def addToIdentifiers(DvIdentifier id)
    {
       identifiers.add(id)
    }
    
    // Nuevo para calcular codedValue
    def beforeInsert() {
       // Para generar XML en una sola linea sin pretty print: http://stackoverflow.com/questions/894625/how-to-disable-pretty-printingwhite-space-newline-in-xstream
       // Interesante: http://www.xml.com/pub/a/2001/06/20/databases.html
       XStream xstream = new XStream()
       xstream.omitField(DataValue.class, "errors");
       codedIdentifiers = xstream.toXML(identifiers)
       codedExternalRef = xstream.toXML(externalRef) // de PartyProxy
    }
    def beforeUpdate() {
       XStream xstream = new XStream()
       xstream.omitField(DataValue.class, "errors");
       codedIdentifiers = xstream.toXML(identifiers)
       codedExternalRef = xstream.toXML(externalRef) // de PartyProxy
    }
    // Al reves
    def afterLoad() {
       XStream xstream = new XStream()
       if (codedIdentifiers) identifiers = xstream.fromXML(codedIdentifiers)
       if (codedExternalRef) externalRef = xstream.fromXML(codedExternalRef) // de PartyProxy
    }
    
    static constraints = {
      name(nullable:true)
      //identifiers(nullable:true)
      codedIdentifiers(nullable:true, maxSize:4096)
    }
}