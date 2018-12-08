# Banker's Algorithm

• This project is a console application providing the implementation of Banker’s algorithm.
• Several processes request and release resources randomly.
• The algorithm will consider requests from n processes for m resources types.
• The system takes the available amount of each resource from the user at the beginning.
• The algorithm will grant a request only if it leaves the system in a safe state.
• A request that leaves the system in an unsafe state will be denied.
• Processes will continually loop requesting and releasing resources from the system.
• The processes request and then release random numbers of resources, which are bounded by
their respective values in the need array.
