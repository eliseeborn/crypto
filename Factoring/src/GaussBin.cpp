// GaussBin.cpp : Defines the entry point for the console application.
//

#include <stdio.h>
#include <stdlib.h>
#include <memory.h>

inline int GET(unsigned long **mas, int i, int j)
{ return ((mas[i][j>>5]>>(j&31))&1);  }

inline int PUT(unsigned long **mas, int i, int j, int k)
{ return mas[i][j>>5]=(k)?(mas[i][j>>5]|(1<<(j&31))):(mas[i][j>>5]&((1<<(j&31))^-1)); }

void GaussBin_Elimination(char * inputname, char * outputname)
{ FILE * in=fopen(inputname,"rb");

  long m, n, i, j, k, l, cols, sum, old, rem=0;
  fscanf(in,"%ld %ld", &n, &m);
  cols=1+(n>>5);

  unsigned long ** mas=new unsigned long*[m];
  for(i=0; i<m; ++i) mas[i]=new unsigned long[cols];
  for(i=0; i<m; ++i) memset(mas[i],0,sizeof(unsigned long)*cols);

// #define GET(i,j) ((mas[i][j>>5]>>(j&31))&1)
// #define PUT(i,j,k) mas[i][j>>5]=(k)?(mas[i][j>>5]|(1<<(j&31))):(mas[i][j>>5]&((1<<(j&31))^-1))

  // Read the input matrix m*n
  for(i=0; i<n; i++)
	  for(j=0; j<m; j++)
	  { fscanf(in, "%ld", &k);
        k%=2;
		if(k) PUT(mas, j,i,k);
	  }

  // Gaussian elimination here
  long * vert=new long[m], *horz=new long[n], *uncl=new long[n], *solv=new long[n];
  memset(vert,0,sizeof(long)*m);
  memset(horz,0,sizeof(long)*n);
  memset(uncl,0,sizeof(long)*n);

  // Here I include removal of columns, which are equal.
  for(i=0; i<n; ++i)
	  for(j=i+1; j<n; ++j)
	  { for(k=0; k<m; k++)
	      if(GET(mas, k,i)!=GET(mas, k,j)) break;
        if(k==m) horz[j]=-1, ++rem; // delete the column j in this case
	  }
  printf("Rejected rows: %ld\n", rem);

  for(i=0; i<n; ++i)
  if(!horz[i])
  { for(j=0; j<m; ++j)
      if(vert[j]==0 && GET(mas, j,i)) break;
    if(j==m) continue;
	vert[j]=1;
	horz[i]=j+1; // horizontal references
	l=j;
	for(j=0; j<m; ++j)
	  if(GET(mas,j,i) && j!=l)
		  for(k=0; k<cols; ++k)
			  mas[j][k]^=mas[l][k];
  }

  for(i=sum=0; i<n; ++i)
	if(--horz[i]==-1) uncl[sum++]=i; // unresolved columns

  FILE * out=fopen(outputname,"wb");
  // Generate resulting vectors
  old=sum;
  if(sum>12)
  { printf("The number of solutions is too many (2^%ld) ... trancated to 2^12!\n", sum);
	sum=12; // too many solutions!!!
  }

  fprintf(out,"%d\n", (1<<sum)-1);

  for(i=1; i<(1<<sum); ++i)
  { memset(solv,0,sizeof(long)*n);
    for(j=sum; j<old; ++j) solv[uncl[j]]=rand()%2;
    for(j=0; j<sum; ++j) // fill unknown variables first
		solv[uncl[j]]=(i>>j)&1;
	// calculate the rest variables here
	for(j=0; j<n; ++j)
		if(horz[j]>=0)
		{ for(l=k=0; l<old; ++l)
			if(GET(mas, horz[j], uncl[l]))  k^=solv[uncl[l]];
		  solv[j]=k;
		}
	// output one solution here
	for(j=0; j<n; j++)
		fprintf(out,"%ld ", solv[j]);
	   fprintf(out,"\n");
  }

  fclose(out);

  for(i=0; i<m; ++i) delete []mas[i];
  delete []mas;
  delete []vert;
  delete []horz;
  delete []uncl;
  fclose(in);
}


int main(int argc, char**argv)
{
	if(argc!=3)
	{ printf("Binary Gausian Elimination Tool.\n"
	         "\n"
	         "Usage: GaussBin.exe <input_file> <output_file>\n"
	         "\n"
			 "<input_file> - description:\n"
			 "M N - first line contains the matrix size M*N\n"
			 "      M denotes the number of (r^2 mod N) - numbers\n"
			 "      N denotes the factor base size\n"
			 "x11 x12 ... x1N     - the integer nonnegative values of the matrix \n"
			 "x21 x22 ... x2N       will be taken modulo 2, and then the Gaussian\n"
			 "   ...                elimination algorithm will be performed.\n"
			 "xM1 xM2 ... xMN\n"
			 "\n"
			 "<output_file> - description:\n"
			 "R   - the number of solutions. If there are a lot of solutions, then\n"
			 "      it will be truncated to 2^12 solutions.\n"
			 "s11 s12 ... s1M     - each line contains a solution vector. i.e. if\n"
			 "s21 s22 ... s2M       we consider solution i, then si1, si2, ..., siM \n"
			 "   ...                denote the correspondent rows from the input_file \n"
			 "sR1 sR2 ... sRM       that we need to sum in order to get (0) modulo 2.\n"
			 "\n"
			 "Cryptology course. Information Technology (c)2003.\n");
	}
	else
	{ GaussBin_Elimination(argv[1], argv[2]);
	}
	return 0;
}
