     F********************************************************************
     ** Cross Navigation Sample
     F********************************************************************

     FINV10     UF   E           K DISK
     D EXPGM           PR                  ExtPgm('ExtPgm')
      /free
          // main()
            SETLL *LOVAL INV10;

            DOU  %EOF(INV10);
                READ  INV10;
                IF  %EOF(INV10) ;
                    *INLR = *ON;
                    LEAVE;
                ENDIF;

                IF ITYPE = 'Y' OR ITYPE = 'Z';
                    IQTY = 50;
                    UPDATE INV10;
                ENDIF;
                DEXPGM(ITYPE:PASS);
 
            ENDDO;

          Begsr *Inzsr;
                //Initialization
          Endsr;
      /end-free
