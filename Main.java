import java.util.*;

class AppointmentRequest {
    int id;
    int arrivalTime;
    int burstTime;
    int originalBurstTime;
    int priority;
    int deadline;
    int startTime;
    int finishTime;
    int waitingTime;
    int turnaroundTime;

    public AppointmentRequest(int id, int arrivalTime, int burstTime, int priority, int deadline) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.originalBurstTime = burstTime; // Keep original burst time for later use
        this.priority = priority;
        this.deadline = deadline;
        this.startTime = -1;
        this.finishTime = -1;
    }
}

class Scheduler {
    List<AppointmentRequest> requests;

    public Scheduler(List<AppointmentRequest> requests) {
        this.requests = requests;
    }

    // First-Come, First-Served Scheduling
    public void fcfs() {
        int currentTime = 0;
        for (AppointmentRequest request : requests) {
            if (currentTime < request.arrivalTime) {
                currentTime = request.arrivalTime;
            }
            request.startTime = currentTime;
            request.finishTime = currentTime + request.burstTime;
            currentTime += request.burstTime;
        }
        calculateMetrics("FCFS");
    }

    // Shortest Job First Scheduling
    public void sjf() {
        List<AppointmentRequest> remainingRequests = new ArrayList<>(requests);
        remainingRequests.sort(Comparator.comparingInt((AppointmentRequest r) -> r.burstTime)
            .thenComparingInt(r -> r.arrivalTime)); // Sorting by burst time and then by arrival time

        int currentTime = 0;
        for (AppointmentRequest request : remainingRequests) {
            if (currentTime < request.arrivalTime) {
                currentTime = request.arrivalTime;
            }
            request.startTime = currentTime;
            request.finishTime = currentTime + request.burstTime;
            currentTime += request.burstTime;
        }
        calculateMetrics("SJF");
    }

    // Round Robin Scheduling
    public void rr(int quantum) {
        Queue<AppointmentRequest> queue = new LinkedList<>();
        int currentTime = 0;
        for (AppointmentRequest request : requests) {
            queue.add(request);
        }

        while (!queue.isEmpty()) {
            AppointmentRequest request = queue.poll();
            if (request.startTime == -1) {
                request.startTime = Math.max(currentTime, request.arrivalTime);
            }
            int timeSlice = Math.min(quantum, request.burstTime);
            currentTime += timeSlice;
            request.burstTime -= timeSlice;

            if (request.burstTime > 0) {
                queue.add(request);
            } else {
                request.finishTime = currentTime;
            }
        }
        calculateMetrics("Round Robin");
    }

    // Priority Scheduling
    public void priorityScheduling() {
        List<AppointmentRequest> remainingRequests = new ArrayList<>(requests);
        remainingRequests.sort(Comparator.comparingInt((AppointmentRequest r) -> r.priority)
            .thenComparingInt(r -> r.arrivalTime)); // Sorting by priority and then by arrival time

        int currentTime = 0;
        for (AppointmentRequest request : remainingRequests) {
            if (currentTime < request.arrivalTime) {
                currentTime = request.arrivalTime;
            }
            request.startTime = currentTime;
            request.finishTime = currentTime + request.burstTime;
            currentTime += request.burstTime;
        }
        calculateMetrics("Priority");
    }

    // Multilevel Feedback Queue Scheduling (Simplified)
    public void multilevelFeedbackQueueScheduling() {
        Queue<AppointmentRequest> queue1 = new LinkedList<>();
        Queue<AppointmentRequest> queue2 = new LinkedList<>();
        int currentTime = 0;

        for (AppointmentRequest request : requests) {
            queue1.add(request);
        }

        while (!queue1.isEmpty() || !queue2.isEmpty()) {
            AppointmentRequest request;
            if (!queue1.isEmpty()) {
                request = queue1.poll();
            } else {
                request = queue2.poll();
            }

            if (request.startTime == -1) {
                request.startTime = Math.max(currentTime, request.arrivalTime);
            }
            int timeSlice = 2;  // Time quantum for first queue
            if (queue2.isEmpty()) {
                timeSlice = Math.min(4, request.burstTime);  // Time quantum for second queue
            }
            request.burstTime -= timeSlice;
            currentTime += timeSlice;

            if (request.burstTime > 0) {
                if (queue2.isEmpty()) {
                    queue2.add(request);
                } else {
                    queue1.add(request);
                }
            } else {
                request.finishTime = currentTime;
            }
        }
        calculateMetrics("Multilevel Feedback Queue");
    }

    // Calculate and display metrics
    public void calculateMetrics(String algorithmName) {
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int n = requests.size();

        for (AppointmentRequest request : requests) {
            request.waitingTime = request.startTime - request.arrivalTime;
            request.turnaroundTime = request.finishTime - request.arrivalTime;
            totalWaitingTime += request.waitingTime;
            totalTurnaroundTime += request.turnaroundTime;
        }

        double avgWaitingTime = (double) totalWaitingTime / n;
        double avgTurnaroundTime = (double) totalTurnaroundTime / n;

        System.out.println("Algorithm: " + algorithmName);
        System.out.println("Average Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
        System.out.println("--------------------------------------------------");
    }
}

public class main {
    public static void main(String[] args) {
        // Create appointment requests
        List<AppointmentRequest> requests = new ArrayList<>();
        requests.add(new AppointmentRequest(1, 0, 5, 3, 10));
        requests.add(new AppointmentRequest(2, 2, 3, 1, 7));
        requests.add(new AppointmentRequest(3, 4, 6, 4, 8));
        requests.add(new AppointmentRequest(4, 6, 2, 2, 5));
        requests.add(new AppointmentRequest(5, 8, 4, 5, 12));

        Scheduler scheduler = new Scheduler(requests);

        // Run different scheduling algorithms
        scheduler.fcfs();
        scheduler.sjf();
        scheduler.rr(2);  // Quantum = 2
        scheduler.priorityScheduling();
        scheduler.multilevelFeedbackQueueScheduling();
    }
}
