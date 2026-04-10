# Use OpenJDK 17 slim as the base image
#FROM openjdk:17-slim
FROM openjdk:24-ea-17-slim-bullseye

# Install dependencies
RUN apt-get update && \
    apt-get install -y curl zip unzip && \
    rm -rf /var/lib/apt/lists/*

# Install SDKMAN! and Maven 3.9.9
RUN curl -s "https://get.sdkman.io" | bash && \
    bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
             sdk install maven 3.9.9"

# Set Maven environment variables
ENV MAVEN_HOME /root/.sdkman/candidates/maven/current
ENV PATH "$MAVEN_HOME/bin:$PATH"

# Verify Maven installation
RUN mvn -version

# Install curl and update CA certificates
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Install Node.js and npm
RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

# Install Playwright dependencies
RUN npx playwright install-deps

# Test internet connectivity
RUN curl -I https://repo.maven.apache.org/maven2/

# Set the working directory inside the container
WORKDIR .

# Copy the Maven project files to the container
COPY . .

# Run Maven to execute the tests
CMD ["mvn", "verify"]
