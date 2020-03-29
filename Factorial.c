#include <stdio.h>
#include <stdlib.h>

#ifdef _OPENMP
#include <omp.h>
#endif

void factorial(int n);
void factorial_serial(int n);

int main(int argc, char* argv[])
{
	int thread_count = strtol(argv[1], NULL, 10);

	while (1)
	{
		printf("Please enter an integer to compute its factorial \n(Enter a negative number or a letter to exit):\n");
		int n;
		if (scanf("%d", &n) == 0 || n < 0 || n>170) 
		{
	    	exit(-1);
		}

	  	double start_s,finish_s,start_p,finish_p,total_s,total_p;

		start_s = omp_get_wtime();
		factorial_serial(n);
		finish_s = omp_get_wtime();

		total_s = finish_s-start_s;

		printf("\nSerial Time = %e seconds.\n", total_s);


	  	start_p = omp_get_wtime();
		factorial(n);
		finish_p = omp_get_wtime();

		total_p = finish_p-start_p;

		printf("\nParallel Time = %e seconds.\n\n", total_p);

		printf("Speedup = %e\n", (total_s/total_p));
		printf("Efficiency = %e\n\n", (total_s/total_p)/thread_count);
		
	}
	return 0;
}


//PARALLEL FUNCTION
void factorial(int n) {
	double factorial = 1;

   	#pragma omp parallel for schedule (guided, 5) reduction(*: factorial) 
		for (int i = 1; i <= n; i++) 
			factorial *= i;
		
	printf("\nThe value of %d! is %.0lf.\n", n, factorial);//omit the decimal part
}

//SERIAL FUNCTION
void factorial_serial(int n) {
	double factorial = 1;

   	for (int i = 1; i <= n; i++) 
		factorial *= i;
		
	printf("\nThe value of %d! is %.0lf.\n", n, factorial);
}
