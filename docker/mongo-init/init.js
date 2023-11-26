conn = new Mongo();


//Adding a user/ profile to Mongo admin table that enable reading and writing data to "movies-db"
db = conn.getDB("admin");
db.createUser(
    {
        user: "yrol",
        pwd: "yrol123",
        roles: [
            {
                role: 'readWrite',
                db: 'movies-db'
            }
        ]
    }
)

// Adding data to movies-db on creation
db = conn.getDB("movies-db");
db.moviesCollection.createIndex({ "uniq_id": 1 }, { unique: true });
db.moviesCollection.insert({
                                   "link": "https://www.netflix.com/watch/80049210",
                                   "name": "Dedh Ishqiya",
                                   "description": "A team of con men fall for a Begum and her female confidante. Does their love fructify?Dedh Ishqiya featuring Naseeruddin Shah and Madhuri Dixit is streaming with subscription on Netflix, streaming with subscription on Hoopla, and available for rent or purchase on Google Play. It's a comedy and drama movie with a better than average IMDb audience rating of 7.0 (7,217 votes).",
                                   "released_at": "2014-01-10",
                                   "genre": "Drama, Romance",
                                   "poster": "https://img.reelgood.com/content/movie/26168898-0c78-4e34-8030-516b3aeb7865/poster-780.jpg",
                                   "streaming_on": "Netflix",
                                   "country": "India",
                                   "number_of_seasons": "",
                                   "type": "Movie",
                                   "content_rating": "18+ (R)",
                                   "imdb_rating": "7/10",
                                   "cast_and_crew": "[{'name': 'Abhishek Chaubey', 'description': 'director'}, {'name': 'Naseeruddin Shah', 'description': 'actor'}, {'name': 'Madhuri Dixit', 'description': 'actor'}, {'name': 'Arshad Warsi', 'description': 'actor'}, {'name': 'Huma Qureshi', 'description': 'actor'}, {'name': 'Vijay Raaz', 'description': 'actor'}, {'name': 'Manoj Pahwa', 'description': 'actor'}, {'name': 'Salman Shahid', 'description': 'actor'}, {'name': 'Raman Maroo', 'description': 'actor'}, {'name': 'Vishal Bhardwaj', 'description': 'actor'}, {'name': 'Abhishek Chaubey', 'description': 'author'}, {'name': 'Vishal Bhardwaj', 'description': 'author'}, {'name': 'Vishal Bhardwaj', 'description': 'actor'}]",
                                   "uniq_id": "e65a1eea-26db-5ca0-a9d0-668c099138dd",
                                   "scraped_at": "02/07/2022 20:36:39"
                               });
db.moviesCollection.insert({
                                   "link": "https://www.netflix.com/watch/81201048",
                                   "name": "Rabbids Invasion - Mission To Mars",
                                   "description": "When Nebulous Industries announces they are recruiting Rabbids for a mission to Mars, Hibernation Rabbid doesn\\u2019t think twice. As a genius Rabbid misunderstood by his stupid peers, he has always dreamed of going to the red planet. He takes off with three other Rabbids: Disco, the lively queen of the dance-floor, Cosmo, the pilot and Mini, the adorable tiny Rabbid. Facing an interplanetary space threat, our heroes will have to learn how to overcome their differences and understand that true wisdom comes from the heart!Rabbids Invasion - Mission To Mars is streaming with subscription on Netflix. It's an animation and comedy movie with an average IMDb audience rating of 5.6 (180 votes).",
                                   "released_at": "2021-09-29",
                                   "genre": "Animation, Comedy",
                                   "poster": "https://img.reelgood.com/content/movie/74904efd-e124-4271-85c5-b8f8dc480766/poster-780.jpg",
                                   "streaming_on": "Netflix",
                                   "country": "France",
                                   "number_of_seasons": "",
                                   "type": "Movie",
                                   "content_rating": "",
                                   "imdb_rating": "5.6/10",
                                   "cast_and_crew": "[{'name': 'Franz Kirchner', 'description': 'director'}, {'name': 'Howard Read', 'description': 'author'}]",
                                   "uniq_id": "07707177-738c-5957-a3e3-0803c42a34c1",
                                   "scraped_at": "02/07/2022 20:36:48"
                               });