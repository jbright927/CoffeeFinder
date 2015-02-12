package com.coffee.finder.util.customquicksort;

import fi.foyt.foursquare.api.entities.CompactVenue;

/**
 * Created by Josh on 10/02/2015.
 */
public class CustomQuickSort {

        public static void sort(CompactVenue[] inputArr) {

            if (inputArr == null || inputArr.length == 0) {
                return;
            }
            quickSort(inputArr, 0, inputArr.length - 1);
        }

        private static void quickSort(CompactVenue[] inputArr, int lowerIndex, int higherIndex) {

            int i = lowerIndex;
            int j = higherIndex;
            // calculate pivot number
            double pivot = inputArr[lowerIndex+(higherIndex-lowerIndex)/2].getLocation().getDistance();
            // Divide into two arrays
            while (i <= j) {
                /**
                 * iterate through and identify numbers from the left side that are
                 * greater than our pivot value. At the same time, identify a number
                 * from the right side which is lower than the pivot value. Once our
                 * search has completed, exchange both numbers.
                 */
                while (inputArr[i].getLocation().getDistance() < pivot) {
                    i++;
                }
                while (inputArr[j].getLocation().getDistance() > pivot) {
                    j--;
                }
                if (i <= j) {
                    exchangeNumbers(inputArr, i, j);
                    //move index to next position on both sides
                    i++;
                    j--;
                }
            }
            // call quickSort() method recursively
            if (lowerIndex < j)
                quickSort(inputArr, lowerIndex, j);
            if (i < higherIndex)
                quickSort(inputArr, i, higherIndex);
        }

        private static void exchangeNumbers(CompactVenue[] inputArr, int i, int j) {
            CompactVenue temp = inputArr[i];
            inputArr[i] = inputArr[j];
            inputArr[j] = temp;
        }

    }
