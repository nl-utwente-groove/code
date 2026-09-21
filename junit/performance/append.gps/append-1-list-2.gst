<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="append-1-list-2">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n10312">
            <attr name="layout">
                <string>284 84 6 14</string>
            </attr>
        </node>
        <node id="n10316">
            <attr name="layout">
                <string>239 148 15 15</string>
            </attr>
        </node>
        <node id="n10311">
            <attr name="layout">
                <string>202 94 6 14</string>
            </attr>
        </node>
        <node id="n10314">
            <attr name="layout">
                <string>227 334 6 14</string>
            </attr>
        </node>
        <node id="n10315">
            <attr name="layout">
                <string>53 146 20 14</string>
            </attr>
        </node>
        <node id="n10317">
            <attr name="layout">
                <string>167 146 15 15</string>
            </attr>
        </node>
        <node id="n10313">
            <attr name="layout">
                <string>53 305 36 28</string>
            </attr>
        </node>
        <edge to="n10311" from="n10311">
            <attr name="label">
                <string>3</string>
            </attr>
        </edge>
        <edge to="n10317" from="n10315">
            <attr name="label">
                <string>list</string>
            </attr>
        </edge>
        <edge to="n10314" from="n10314">
            <attr name="label">
                <string>4</string>
            </attr>
        </edge>
        <edge to="n10315" from="n10313">
            <attr name="label">
                <string>caller</string>
            </attr>
        </edge>
        <edge to="n10314" from="n10313">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge to="n10313" from="n10313">
            <attr name="label">
                <string>control</string>
            </attr>
        </edge>
        <edge to="n10317" from="n10313">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge to="n10313" from="n10313">
            <attr name="label">
                <string>append</string>
            </attr>
        </edge>
        <edge to="n10312" from="n10312">
            <attr name="label">
                <string>5</string>
            </attr>
        </edge>
        <edge to="n10315" from="n10315">
            <attr name="label">
                <string>root</string>
            </attr>
        </edge>
        <edge to="n10312" from="n10316">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
        <edge to="n10316" from="n10317">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n10311" from="n10317">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
    </graph>
</gxl>
