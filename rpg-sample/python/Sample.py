# 
# /home/madytyoo/Downloads/xcode-navigation/rpg-sample/rpgle/Sample.rpgle
# 
import logging, argparse

from decimal import *
from runtime import Blank
from runtime import Subst
from runtime import Found
from runtime import Context
from runtime import Constants
from files.INV10 import Inv10

class Sample:
    def __init__(self, context):
        self.context = context
        self.inv10 = Inv10(context)
    
    def Inzsr(self):
        return
    
    def run(self):
        # Initialization
        
        self.Inzsr()
        self.inv10.setLL(Constants.LOVAL)
        while True:
            self.inv10.read()
            if self.inv10.EOF():
                self.context.indicators.inlr = Constants.ON
                break
            if self.inv10.itype == 'Y' or self.inv10.itype == 'Z':
                self.inv10.iqty = 50
                self.inv10.update()
            Expgm(context).run(inv10.type)
            if self.inv10.EOF():
                break
    

if __name__ == '__main__':
    context = Context()
    sample = Sample(context)
    sample.run()