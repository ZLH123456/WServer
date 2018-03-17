# WServer

WServer implementation that accepts petitions in form of HTTP Request and serves with the files asked (e.g.: http://localhost:8302/index.html).

The application accepts parameters in the following form: '?[parameter]=[boolean]'. To add more parameters just add '&' after the first parameter (e.g.: http://localhost:8302/index.html?asc=true&gzip=true&zip=true).

The parameters that are accepted are: 

	* ascii (asc) - transforms a html file to a plain text file deleting all of the html tags.
	* gzip (gzip) - compresses the file asked into a gzip file.
	* zip (zip) - compresses the file asked into a zip file.