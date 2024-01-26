package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationPipeline;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections

	@Autowired
	private MongoTemplate template;

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 * 
	 * 
	 * db.listings.aggregate([
			
			{
				$match: {
				"address.country": {
					$regex: "^australia$",
					$options: "i"
				}
				}
			},
	
			{
				$project: {
				_id: 1, 
				suburb: "$address.suburb",
				
				}
			}
			]);


	 *
	 */
	 public List<String> getSuburbs(String country) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("address.country")
                        .regex("^" + country + "$", "i")),
                Aggregation.project("_id", "address.suburb")
        );

        System.out.println(aggregation.toString());

        AggregationResults<Document> results = template.aggregate(aggregation, "listings", Document.class);
        List<String> suburbs = results.getMappedResults()
                .stream()
                .map(document -> document.getString("suburb"))
                .filter(suburb -> suburb != null && !suburb.isEmpty()) 
                .collect(Collectors.toList());

        System.out.println(suburbs);
        return suburbs;
    }


	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *
				*var suburb = "YourSuburb";
			var persons = 5; // Replace with actual value
			var duration = 7; // Replace with actual value
			var priceRange = 1000.0; // Replace with actual value

			var pipeline = [
			{
				$sort: { price: -1 }
			},
			{
				$limit: 10
			},
			{
				$project: {
				name: 1,
				accommodates: 1,
				price: 1
				}
			},
			{
				$lookup: {
				from: "reviews",
				localField: "suburb",
				foreignField: "suburb",
				as: "reviews"
				}
			},
			{
				$unwind: "$reviews"
			},
			{
				$project: {
				id: 1,
				name: 1,
				accommodates: 1,
				price: 1,
				rating: "$reviews.rating",
				user: "$reviews.user",
				comment: "$reviews.comment",
				review_id: "$reviews._id"
				}
			},
			{
				$match: {
				price: { $lte: priceRange },
				accommodates: { $gte: persons },
				min_night: { $lte: duration },
				suburb: { $regex: suburb, $options: "i" }
				}
			}
			];

			db.listings.aggregate(pipeline);

				*/


	 
	 public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
        ProjectionOperation projOps = Aggregation.project("name", "accommodates", "min_night");
        SortOperation sortOps = Aggregation.sort(Sort.by(Direction.DESC, "price"));
        LimitOperation limitOps = Aggregation.limit(10);

        LookupOperation lookUpOps = LookupOperation.newLookup()
                .from("listing")             
                .localField("suburb")       
                .foreignField("address.suburb") 
                .as("listings");

        UnwindOperation unwindOps = Aggregation.unwind("listings");

        ProjectionOperation finalProjOps = Aggregation.project("id", "name", "accommodates", "price");

        Aggregation pipeline = Aggregation.newAggregation(lookUpOps, unwindOps, finalProjOps, sortOps,
                Aggregation.match(
                        Criteria.where("price").lte(priceRange)
                                .and("accommodates").gte(persons)
                                .and("min_night").lte(duration)
                                .and("listings.suburb").regex(suburb, "i") 
                )
        );

        List<Document> results = template.aggregate(pipeline, "listings", Document.class).getMappedResults();
    	 return results
		 .stream()
		 .map(doc -> Utils.toAccommodationSummary(doc))
		 .toList();

    }


	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
