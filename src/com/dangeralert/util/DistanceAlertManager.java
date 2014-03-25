package com.dangeralert.util;

public class DistanceAlertManager {
	
	public float getAlertDistanceKm(float velocity){
		
		if(velocity >= 0.0 && velocity <=15.0){ // entre 0 e 15k/h = 50m
			return 50;
		}
		else{
			if(velocity >= 15.0 && velocity <=30.0){
				return 100;
			}
			else{
				if(velocity >= 30.0 && velocity <=55.0){
					return 280;
				}
				else{
					if(velocity >= 55.0 && velocity <=85.0){
						return 350;
					}
					else{
						if(velocity >= 85.0 && velocity <=130.0){
							return 420;
						}
						else{
							if(velocity >= 130.0 && velocity <=180.0){
								return 700;
							}
							else{
								if(velocity >= 180.0)
									return 1500;
								}
							}
						}
					}
				}
			}
		return 40;
	}
	
	public float getAlertDistanceMt(float velocity){
		
		if(velocity >= 0.0 && velocity <=4.15){ // entre 0 e 4.15m/s = 50m
			return 50;
		}
		else{
			if(velocity >= 4.15 && velocity <=8.36){
				return 100;
			}
			else{
				if(velocity >= 8.36 && velocity <=15.28){
					return 280;
				}
				else{
					if(velocity >= 15.28 && velocity <=23.62){
						return 350;
					}
					else{
						if(velocity >= 23.62 && velocity <=36.12){
							return 420;
						}
						else{
							if(velocity >= 36.12 && velocity <=50){
								return 700;
							}
							else{
								if(velocity >= 50)
									return 1500;
								}
							}
						}
					}
				}
			}
		return 40;
	}
}
