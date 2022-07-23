import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CheckinService } from 'src/app/services/checkin.service';

@Component({
  selector: 'app-checkin',
  templateUrl: './checkin.component.html',
  styleUrls: ['./checkin.component.css'],
})
export class CheckinComponent implements OnInit {
  noOfbags!: Number;
  data: any;

  constructor(private service: CheckinService, private router: Router) {}

  ngOnInit(): void {
    this.data = this.service.reservationData;
  }

  public checkIn() {
    let request = {
      id: this.data.id,
      checkIn: true,
      noOfBags: this.noOfbags,
    };

    this.service.checkIn(request).subscribe((res: any) => {
      this.router.navigate(['/confirm']);
    });
  }
}
