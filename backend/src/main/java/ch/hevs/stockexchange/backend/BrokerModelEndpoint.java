package ch.hevs.stockexchange.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "brokerModelApi",
        version = "v1",
        resource = "brokerModel",
        namespace = @ApiNamespace(
                ownerDomain = "backend.stockexchange.hevs.ch",
                ownerName = "backend.stockexchange.hevs.ch",
                packagePath = ""
        )
)
public class BrokerModelEndpoint {

    private static final Logger logger = Logger.getLogger(BrokerModelEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(BrokerModel.class);
    }

    /**
     * Returns the {@link BrokerModel} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code BrokerModel} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "brokerModel/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public BrokerModel get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting BrokerModel with ID: " + id);
        BrokerModel brokerModel = ofy().load().type(BrokerModel.class).id(id).now();
        if (brokerModel == null) {
            throw new NotFoundException("Could not find BrokerModel with ID: " + id);
        }
        return brokerModel;
    }

    /**
     * Inserts a new {@code BrokerModel}.
     */
    @ApiMethod(
            name = "insert",
            path = "brokerModel",
            httpMethod = ApiMethod.HttpMethod.POST)
    public BrokerModel insert(BrokerModel brokerModel) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that brokerModel.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(brokerModel).now();
        logger.info("Created BrokerModel with ID: " + brokerModel.getId());

        return ofy().load().entity(brokerModel).now();
    }

    /**
     * Updates an existing {@code BrokerModel}.
     *
     * @param id          the ID of the entity to be updated
     * @param brokerModel the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code BrokerModel}
     */
    @ApiMethod(
            name = "update",
            path = "brokerModel/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public BrokerModel update(@Named("id") long id, BrokerModel brokerModel) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(brokerModel).now();
        logger.info("Updated BrokerModel: " + brokerModel);
        return ofy().load().entity(brokerModel).now();
    }

    /**
     * Deletes the specified {@code BrokerModel}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code BrokerModel}
     */
    @ApiMethod(
            name = "remove",
            path = "brokerModel/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(BrokerModel.class).id(id).now();
        logger.info("Deleted BrokerModel with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "brokerModel",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<BrokerModel> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<BrokerModel> query = ofy().load().type(BrokerModel.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<BrokerModel> queryIterator = query.iterator();
        List<BrokerModel> brokerModelList = new ArrayList<BrokerModel>(limit);
        while (queryIterator.hasNext()) {
            brokerModelList.add(queryIterator.next());
        }
        return CollectionResponse.<BrokerModel>builder().setItems(brokerModelList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(long id) throws NotFoundException {
        try {
            ofy().load().type(BrokerModel.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find BrokerModel with ID: " + id);
        }
    }
}