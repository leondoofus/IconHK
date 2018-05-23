import numpy as np
import matplotlib.pyplot as pl

d1 = [0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 24, 25, 26, 28]
d2 = [0, -2, -4, -5, -7, -8, -9, -9, -10, -10, -9, -9, -8, -7, -5, -4, -2, 0, 2, 5, 8, 11, 15, 19, 23, 28]
d3 = [0, 6, 12, 16, 19, 22, 23, 24, 25, 25, 24, 23, 22, 20, 19, 18, 17, 16, 15, 15, 15, 16, 18, 20, 23, 28]
index = [i for i in range(26)]
dt1 = [i for i in d1]
dt1[25] = 25
dt2 = []
for i in range(26):
	dt2.append(d2[i]) if d2[i] >= 0 else dt2.append(0) 
dt2[25] = 25

dt3 = [i for i in d3]
dt3[25] = 25

pl.figure()
pl.plot(index,dt1,'r*-')
pl.plot(index,d1,'g*-')
pl.title('Linear')

pl.figure()
pl.plot(index,dt2,'r*-')
pl.plot(index,d2,'g*-')
pl.title('Quadratic')

pl.figure()
pl.plot(index,dt3,'r*-')
pl.plot(index,d3,'g*-')
pl.title('Cubic')
pl.show()