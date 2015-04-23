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
        name = "currencyModelApi",
        version = "v1",
        resource = "currencyModel",
        namespace = @ApiNamespace(
                ownerDomain = "backend.stockexchange.hevs.ch",
                ownerName = "backend.stockexchange.hevs.ch",
                packagePath = ""
        )
)
public class CurrencyModelEndpoint {

    private static final Logger logger = Logger.getLogger(CurrencyModelEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(CurrencyModel.class);
    }

    /**
     * Returns the {@link CurrencyModel} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code CurrencyModel} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "currencyModel/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CurrencyModel get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting CurrencyModel with ID: " + id);
        CurrencyModel currencyModel = ofy().load().type(CurrencyModel.class).id(id).now();
        if (currencyModel == null) {
            throw new NotFoundException("Could not find CurrencyModel with ID: " + id);
        }
        return currencyModel;
    }

    /**
     * Inserts a new {@code CurrencyModel}.
     */
    @ApiMethod(
            name = "insert",
            path = "currencyModel",
            httpMethod = ApiMethod.HttpMethod.POST)
    public CurrencyModel insert(CurrencyModel currencyModel) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that currencyModel.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(currencyModel).now();
        logger.info("Created CurrencyModel with ID: " + currencyModel.getId());

        return ofy().load().entity(currencyModel).now();
    }

    /**
     * Updates an existing {@code CurrencyModel}.
     *
     * @param id            the ID of the entity to be updated
     * @param currencyModel the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code CurrencyModel}
     */
    @ApiMethod(
            name = "update",
            path = "currencyModel/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public CurrencyModel update(@Named("id") long id, CurrencyModel currencyModel) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(currencyModel).now();
        logger.info("Updated CurrencyModel: " + currencyModel);
        return ofy().load().entity(currencyModel).now();
    }

    /**
     * Deletes the specified {@code CurrencyModel}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code CurrencyModel}
     */
    @ApiMethod(
            name = "remove",
            path = "currencyModel/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(CurrencyModel.class).id(id).now();
        logger.info("Deleted CurrencyModel with ID: " + id);
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
            path = "currencyModel",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<CurrencyModel> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<CurrencyModel> query = ofy().load().type(CurrencyModel.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<CurrencyModel> queryIterator = query.iterator();
        List<CurrencyModel> currencyModelList = new ArrayList<CurrencyModel>(limit);
        while (queryIterator.hasNext()) {
            currencyModelList.add(queryIterator.next());
        }
        return CollectionResponse.<CurrencyModel>builder().setItems(currencyModelList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(long id) throws NotFoundException {
        try {
            ofy().load().type(CurrencyModel.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find CurrencyModel with ID: " + id);
        }
    }
}