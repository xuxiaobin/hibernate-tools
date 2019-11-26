package org.hibernate.tool.internal.reveng.binder;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.api.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.api.reveng.TableIdentifier;

public class ForeignKeyBinder {
	
	private static Logger LOGGER = Logger.getLogger(ForeignKeyBinder.class.getName());
	
	public static ForeignKeyBinder create(
			MetadataBuildingContext metadataBuildingContext,
			InFlightMetadataCollector metadataCollector,
			ReverseEngineeringStrategy revengStrategy,
			String defaultCatalog,
			String defaultSchema) {
		return new ForeignKeyBinder(
				metadataBuildingContext, 
				metadataCollector, 
				revengStrategy, 
				defaultCatalog, 
				defaultSchema);
	}
	
	private MetadataBuildingContext metadataBuildingContext;
	private InFlightMetadataCollector metadataCollector;
	private ReverseEngineeringStrategy revengStrategy;
	private String defaultCatalog;
	private String defaultSchema;
	
	private ForeignKeyBinder(
			MetadataBuildingContext metadataBuildingContext,
			InFlightMetadataCollector metadataCollector,
			ReverseEngineeringStrategy revengStrategy,
			String defaultCatalog,
			String defaultSchema) {
		this.metadataBuildingContext = metadataBuildingContext;
		this.metadataCollector = metadataCollector;
		this.revengStrategy = revengStrategy;
		this.defaultCatalog = defaultCatalog;
		this.defaultSchema = defaultSchema;
	}
	
	public void bind(
			ForeignKey foreignKey,
			PersistentClass persistentClass,
			Set<Column> processed,
			Mapping mapping) {
		if(revengStrategy.excludeForeignKeyAsCollection(
				foreignKey.getName(),
				TableIdentifier.create(foreignKey.getTable() ),
				foreignKey.getColumns(),
				TableIdentifier.create(foreignKey.getReferencedTable() ),
				foreignKey.getReferencedColumns())) {
			LOGGER.log(Level.INFO, "Rev.eng excluded one-to-many or one-to-one for foreignkey " + foreignKey.getName());
		} else if (revengStrategy.isOneToOne(foreignKey)){
        	Property property = OneToOneBinder
        			.create(
        					metadataBuildingContext, 
        					revengStrategy, 
        					defaultCatalog, 
        					defaultSchema)
        			.bind(
        					persistentClass, 
        					foreignKey.getTable(), 
        					foreignKey, 
        					processed, 
        					false, 
        					true);
			persistentClass.addProperty(property);
		} else {
			Property property = OneToManyBinder
					.create(
							metadataBuildingContext, 
							metadataCollector, 
							revengStrategy, 
							defaultCatalog, 
							defaultSchema)
					.bind(
							persistentClass, 
							foreignKey, 
							processed, 
							mapping);
			persistentClass.addProperty(property);
		}		
	}

}