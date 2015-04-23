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
        name = "exchangeRateModelApi",
        version = "v1",
        resource = "exchangeRateModel",
        namespace = @ApiNamespace(
                ownerDomain = "backend.stockexchange.hevs.ch",
                ownerName = "backend.stockexchange.hevs.ch",
                packagePath = ""
        )
)
public class ExchangeRateModelEndpoint {

    private static final Logger logger = Logger.getLogger(ExchangeRateModelEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(ExchangeRateModel.class);
    }

    /**
     * Returns the {@link ExchangeRateModel} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code ExchangeRateModel} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "exchangeRateModel/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public ExchangeRateModel get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting ExchangeRateModel with ID: " + id);
        ExchangeRateModel exchangeRateModel = ofy().load().type(ExchangeRateModel.class).id(id).now();
        if (exchangeRateModel == null) {
            throw new NotFoundException("Could not find ExchangeRateModel with ID: " + id);
        }
        return exchangeRateModel;
    }

    /**
     * Inserts a new {@code ExchangeRateModel}.
     */
    @ApiMethod(
            name = "insert",
            path = "exchangeRateModel",
            httpMethod = ApiMethod.HttpMethod.POST)
    public ExchangeRateModel insert(ExchangeRateModel exchangeRateModel) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that exchangeRateModel.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(exchangeRateModel).now();
        logger.info("Created ExchangeRateModel with ID: " + exchangeRateModel.getId());

        return ofy().load().entity(exchangeRateModel).now();
    }

    /**
     * Updates an existing {@code ExchangeRateModel}.
     *
     * @param id                the ID of the entity to be updated
     * @param exchangeRateModel the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code ExchangeRateModel}
     */
    @ApiMethod(
            name = "update",
            path = "exchangeRateModel/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public ExchangeRateModel update(@Named("id") Long id, ExchangeRateModel exchangeRateModel) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(exchangeRateModel).now();
        logger.info("Updated ExchangeRateModel: " + exchangeRateModel);
        return ofy().load().entity(exchangeRateModel).now();
    }

    /**
     * Deletes the specified {@code ExchangeRateModel}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code ExchangeRateModel}
     */
    @ApiMethod(
            name = "remove",
            path = "exchangeRateModel/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(ExchangeRateModel.class).id(id).now();
        logger.info("Deleted ExchangeRateModel with ID: " + id);
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
            path = "exchangeRateModel",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<ExchangeRateModel> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<ExchangeRateModel> query = ofy().load().type(ExchangeRateModel.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<ExchangeRateModel> queryIterator = query.iterator();
        List<ExchangeRateModel> exchangeRateModelList = new ArrayList<ExchangeRateModel>(limit);
        while (queryIterator.hasNext()) {
            exchangeRateModelList.add(queryIterator.next());
        }
        return CollectionResponse.<ExchangeRateModel>builder().setItems(exchangeRateModelList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(ExchangeRateModel.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find ExchangeRateModel with ID: " + id);
        }
    }
}