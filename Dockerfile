# Fetch clojure image as the base
FROM clojure:latest

# Create a directory for the app to run from
WORKDIR /app

# Copy the entire folder contents to the image
COPY . .

# Set Evironment variables
ENV PORT=8080

# Expose port to the local machine
EXPOSE 8080

# Run the container calling the main function
CMD [ "clojure", "-M", "-m", "musab.core" ]